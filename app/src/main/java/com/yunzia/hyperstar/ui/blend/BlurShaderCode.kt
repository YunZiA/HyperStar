package com.yunzia.hyperstar.ui.blend


val blurShaderCode = """
uniform shader inputImage;
uniform float2 resolution;

uniform int enableBlur;   
uniform float blurRadius;

uniform int layerCount;
uniform int blendModes[8];
uniform vec4 layerColors[8];

half3 blendColorDodge(half3 base, half3 blend) {
    half3 denominator = max(half3(0.0001), 1.0 - blend);
    return min(half3(1.0), base / denominator);
}
half3 blendColorBurn(half3 base, half3 blend) {
    half3 denominator = max(half3(0.0001), blend);
    return 1.0 - min(half3(1.0), (1.0 - base) / denominator);
}
half3 blendLinearLight(half3 base, half3 blend) {
    return 2.0 * blend + base - 1.0;
}
half3 rgb2xyz(half3 c) {
    const half3x3 mat = half3x3(
        0.4124, 0.3576, 0.1805,
        0.2126, 0.7152, 0.0722,
        0.0193, 0.1192, 0.9505
    );
    return c * mat;
}
half3 xyz2lab(half3 xyz) {
    xyz /= half3(0.95047, 1.0, 1.08883);
    half3 f;
    f.r = xyz.r > 0.008856 ? pow(xyz.r, 1.0/3.0) : 7.787 * xyz.r + 16.0/116.0;
    f.g = xyz.g > 0.008856 ? pow(xyz.g, 1.0/3.0) : 7.787 * xyz.g + 16.0/116.0;
    f.b = xyz.b > 0.008856 ? pow(xyz.b, 1.0/3.0) : 7.787 * xyz.b + 16.0/116.0;
    return half3(116.0 * f.g - 16.0, 500.0 * (f.r - f.g), 200.0 * (f.g - f.b));
}
half3 lab2xyz(half3 lab) {
    half3 f;
    f.g = (lab.x + 16.0) / 116.0;
    f.r = lab.y / 500.0 + f.g;
    f.b = f.g - lab.z / 200.0;
    half3 xyz;
    xyz.r = f.r > 0.20689 ? f.r*f.r*f.r : (f.r - 16.0/116.0) / 7.787;
    xyz.g = f.g > 0.20689 ? f.g*f.g*f.g : (f.g - 16.0/116.0) / 7.787;
    xyz.b = f.b > 0.20689 ? f.b*f.b*f.b : (f.b - 16.0/116.0) / 7.787;
    return xyz * half3(0.95047, 1.0, 1.08883);
}
half3 rgb2lab(half3 rgb) {
    half3 lab = xyz2lab(rgb2xyz(rgb));  // lab.x = 0~100, lab.yz ≈ -128~127
    // 归一化到 [0,1] 范围
    lab.x /= 100.0;                     // L: 0~100 → 0~1
    lab.y = (lab.y + 128.0) / 255.0;    // a: -128~127 → 0~1
    lab.z = (lab.z + 128.0) / 255.0;    // b: -128~127 → 0~1
    return clamp(lab, 0.0, 1.0);
}
half3 lab2rgb(half3 lab) {
    // 反归一化：从 [0,1] 恢复到 Lab 原始范围
    half L = lab.x * 100.0;
    half a = lab.y * 255.0 - 128.0;
    half b = lab.z * 255.0 - 128.0;
    half3 labRaw = half3(L, a, b);

    half3 xyz = lab2xyz(labRaw);
    half3x3 inv = half3x3(
         3.2406, -1.5372, -0.4986,
        -0.9689,  1.8758,  0.0415,
         0.0557, -0.2040,  1.0570
    );
    half3 rgb = xyz * inv;
    return clamp(rgb, 0.0, 1.0);
}
half blendOverlay(half base, half blend) {
    if (base < 0.5) {
        return 2.0 * base * blend;
    } else {
        return 1.0 - 2.0 * (1.0 - base) * (1.0 - blend);
    }
}
half3 blendLabLightenWithGreyscale(half3 base, half3 blend) {
    half3 baseLab = rgb2lab(base);
    half3 blendLab = rgb2lab(blend);
    half L = 1.0 - (1.0 - baseLab.x) * (1.0 - blendLab.x);
    half A = blendOverlay(baseLab.y, blendLab.y);
    half B = blendOverlay(baseLab.z, blendLab.z);
    return lab2rgb(half3(L, A, B));
}
half3 blendLabDarkenWithGreyscale(half3 base, half3 blend) {
    half3 baseLab = rgb2lab(base);
    half3 blendLab = rgb2lab(blend);
    half L = baseLab.x * blendLab.x;  
    half A = blendOverlay(baseLab.y, blendLab.y);
    half B = blendOverlay(baseLab.z, blendLab.z);
    return lab2rgb(half3(L, A, B));
}
half3 blendLab(half3 base, half3 blend) {
    half3 baseLab = rgb2lab(base);
    half3 blendLab = rgb2lab(blend);
    
    half L = (baseLab.x + blendLab.x) * 0.5;
    half a = baseLab.y ;
    half b = baseLab.z ;
    half3 resultLab = half3(L, a, b);
    
    //resultLab.x = min(0.95, max(0.05, resultLab.x)); // 限制亮度在 5%~95%
    
    return lab2rgb(resultLab);
}
half3 blendLinearLightLab(half3 base, half3 blend) {
    half3 baseLab = rgb2lab(base);
    half3 blendLab = rgb2lab(blend);
    half L = baseLab.x + 2.0 * blendLab.x - 1.0;
    L = clamp(L, 0.0, 1.0);
    half A = blendLab.y;
    half B = blendLab.z;
    return lab2rgb(half3(L, A, B));
}
half3 blendByMode(half3 base, half3 blend, int mode) {
    if (mode == 18) return blendColorDodge(base, blend);
    else if (mode == 19) return blendColorBurn(base, blend);
    else if (mode == 100) return blendLinearLight(base, blend);
    else if (mode == 103) return blendLabLightenWithGreyscale(base, blend);
    else if (mode == 105) return blendLabDarkenWithGreyscale(base, blend);
    else if (mode == 106) return blendLab(base, blend);
    else if (mode == 107) return blendLinearLightLab(base, blend);
    return blend;
}

half4 blurHorizontal(float2 uv, float radius) {
    const int K = 3; // 7次采样
    float stepX = 1.0 / resolution.x;
    half4 result = half4(0.0);
    float totalWeight = 0.0;
    float sigma = max(radius / 2.0, 0.5);
    
    for (int i = -K; i <= K; i++) {
        float weight = exp(-float(i*i) / (2.0 * sigma * sigma));
        float2 offset = float2(float(i) * stepX * radius, 0.0);
        result += inputImage.eval(uv + offset) * weight;
        totalWeight += weight;
    }
    return result / max(totalWeight, 1e-6);
}

half4 blurVertical(float2 uv, float radius) {
    const int K = 3;                     // 半核大小 = 3，总采样 7 次
    float stepY = 1.0 / resolution.y;    // 垂直步长（不加 const）
    half4 result = half4(0.0);
    float totalWeight = 0.0;
    float sigma = max(radius / 2.0, 0.5); // 标准差，控制模糊强度
    
    for (int i = -K; i <= K; i++) {
        float weight = exp(-float(i*i) / (2.0 * sigma * sigma));
        float2 offset = float2(0.0, float(i) * stepY * radius); // 垂直偏移
        result += inputImage.eval(uv + offset) * weight;
        totalWeight += weight;
    }
    return result / max(totalWeight, 1e-6);
}

half4 main(float2 fragCoord) {
    half4 currentColor;
    
    if (enableBlur == 1 && blurRadius > 0.0) {
        currentColor = blurHorizontal(fragCoord, blurRadius);
        currentColor = blurVertical(fragCoord, blurRadius);
    } else {
        currentColor = inputImage.eval(fragCoord);
    }

    for (int i = 0; i < 8; i++) {
        if (i >= layerCount) break;
        half4 layerColor = layerColors[i];
        int mode = blendModes[i];
        half3 blendedRgb = blendByMode(currentColor.rgb, layerColor.rgb, mode);
        //currentColor = half4(blendedRgb, currentColor.a);
        half3 dstRgb = currentColor.rgb;
        half dstA = currentColor.a;
        half srcA = layerColor.a;
        half3 srcRgb = blendedRgb;
        half3 resultRgb = srcRgb * srcA + dstRgb * (1.0 - srcA);
        half resultA = srcA + dstA * (1.0 - srcA);
        currentColor = half4(resultRgb, resultA);
    }
    return currentColor;
}
""".trim()