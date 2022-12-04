package io.github.darkkronicle.facelift.ui.config;

import io.github.darkkronicle.facelift.image.CustomImage;
import io.github.darkkronicle.facelift.image.ImageHandler;
import io.github.darkkronicle.facelift.ui.components.CleanButtonComponent;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import lombok.Getter;
import net.minecraft.util.Util;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class BackgroundSelector extends CleanButtonComponent {

    @Getter
    private File file;
    private File imageFile;
    private CustomImage image;

    public BackgroundSelector(
            File file, int cornerRadius, int samples,
            Consumer<CleanButtonComponent> onPress
    ) {
        super(Sizing.content(), cornerRadius, samples, onPress);
        this.file = file;
        horizontalAlignment(HorizontalAlignment.CENTER);

        if (file.isDirectory()) {
            File[] files = this.file.listFiles(f -> f.getName().endsWith(".jpg") || f.getName().endsWith(".png"));
            for (File f : files) {
                String name = FilenameUtils.getBaseName(f.getName());
                if (name.endsWith("0")) {
                    this.imageFile = f;
                    break;
                }
            }
        } else {
            this.imageFile = file;
        }
        padding(Insets.of(3));
        if (this.imageFile != null) {
            image = ImageHandler.getInstance().lazyLoadAsync(imageFile, Util.getMainWorkerExecutor());
            if (image == null) {
                image = ImageHandler.getInstance().loadImage(imageFile);
            }
        }
        texture(image, 0, 0, 120, 80, 120, 80, Insets.bottom(2), null);
    }



}
