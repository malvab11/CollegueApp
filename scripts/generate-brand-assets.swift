// Run from the repository root: swift scripts/generate-brand-assets.swift
// Reuses the supplied crest; only white margins and output dimensions change.
import Foundation
import CoreGraphics
import ImageIO

let root = URL(fileURLWithPath: FileManager.default.currentDirectoryPath)
let source = root.appendingPathComponent("app/src/main/res/mipmap-xxxhdpi/ic_logo.png")
let input = CGImageSourceCreateWithURL(source as CFURL, nil)!
let original = CGImageSourceCreateImageAtIndex(input, 0, nil)!
let width = original.width, height = original.height
let pixels = UnsafeMutablePointer<UInt8>.allocate(capacity: width * height * 4)
defer { pixels.deallocate() }
let scan = CGContext(data: pixels, width: width, height: height, bitsPerComponent: 8, bytesPerRow: width * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue | CGBitmapInfo.byteOrder32Big.rawValue)!
scan.draw(original, in: CGRect(x: 0, y: 0, width: width, height: height))
var left = width, right = 0, bottom = height, top = 0
for y in 0..<height {
    for x in 0..<width {
        let offset = (y * width + x) * 4
        if pixels[offset + 3] > 0 && min(pixels[offset], pixels[offset + 1], pixels[offset + 2]) < 220 {
            left = min(left, x); right = max(right, x)
            bottom = min(bottom, y); top = max(top, y)
        }
    }
}
let crest = original.cropping(to: CGRect(x: left, y: bottom, width: right - left + 1, height: top - bottom + 1))!
func write(_ path: String, size: Int, coverage: Double) {
    let context = CGContext(data: nil, width: size, height: size, bitsPerComponent: 8, bytesPerRow: size * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.noneSkipLast.rawValue)!
    context.setFillColor(CGColor(gray: 1, alpha: 1))
    context.fill(CGRect(x: 0, y: 0, width: size, height: size))
    context.interpolationQuality = .high
    let scale = Double(size) * coverage / Double(max(crest.width, crest.height))
    let w = Double(crest.width) * scale, h = Double(crest.height) * scale
    context.draw(crest, in: CGRect(x: (Double(size) - w) / 2, y: (Double(size) - h) / 2, width: w, height: h))
    let url = root.appendingPathComponent(path)
    try! FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
    let output = CGImageDestinationCreateWithURL(url as CFURL, "public.png" as CFString, 1, nil)!
    CGImageDestinationAddImage(output, context.makeImage()!, nil)
    precondition(CGImageDestinationFinalize(output))
}
write("shared/src/commonMain/composeResources/drawable/ic_logo.png", size: 768, coverage: 0.92)
write("iosApp/Collegue/Assets.xcassets/AppIcon.appiconset/AppIcon.png", size: 1024, coverage: 0.82)
write("app/src/main/ic_launcher-playstore.png", size: 512, coverage: 0.82)
for (density, size) in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)] {
    let folder = "app/src/main/res/mipmap-\(density)"
    for name in ["ic_launcher", "ic_launcher_round", "ic_launcher_foreground"] {
        try? FileManager.default.removeItem(at: root.appendingPathComponent("\(folder)/\(name).webp"))
    }
    write("\(folder)/ic_launcher.png", size: size, coverage: 0.82)
    write("\(folder)/ic_launcher_round.png", size: size, coverage: 0.72)
    write("\(folder)/ic_launcher_foreground.png", size: size * 108 / 48, coverage: 0.58)
}
print("Institutional crest exported for shared UI, Android and iOS.")
