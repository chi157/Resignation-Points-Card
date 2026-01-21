# Load System.Drawing assembly
Add-Type -AssemblyName System.Drawing

$sourceImage = "e:\My Project\AndroidStudioProjects\ResignationPointsCard\app\src\main\res\drawable\ic_launcher_original.png"
$baseDir = "e:\My Project\AndroidStudioProjects\ResignationPointsCard\app\src\main\res"

# Define sizes for different densities
$sizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

# Load source image
$srcImage = [System.Drawing.Image]::FromFile($sourceImage)

foreach ($density in $sizes.Keys) {
    $size = $sizes[$density]
    $outputPath = Join-Path $baseDir "$density\ic_launcher.png"
    
    # Create new bitmap with specified size
    $destImage = New-Object System.Drawing.Bitmap($size, $size)
    $graphics = [System.Drawing.Graphics]::FromImage($destImage)
    
    # Set high quality rendering
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    
    # Draw resized image
    $graphics.DrawImage($srcImage, 0, 0, $size, $size)
    
    # Save as PNG
    $destImage.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    
    # Cleanup
    $graphics.Dispose()
    $destImage.Dispose()
    
    Write-Host "Created: $outputPath ($size x $size)"
}

# Also create round icon
foreach ($density in $sizes.Keys) {
    $size = $sizes[$density]
    $outputPath = Join-Path $baseDir "$density\ic_launcher_round.png"
    
    # Create new bitmap with specified size
    $destImage = New-Object System.Drawing.Bitmap($size, $size)
    $graphics = [System.Drawing.Graphics]::FromImage($destImage)
    
    # Set high quality rendering
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    
    # Draw resized image
    $graphics.DrawImage($srcImage, 0, 0, $size, $size)
    
    # Save as PNG
    $destImage.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    
    # Cleanup
    $graphics.Dispose()
    $destImage.Dispose()
    
    Write-Host "Created: $outputPath ($size x $size)"
}

$srcImage.Dispose()
Write-Host "All icons created successfully!"
