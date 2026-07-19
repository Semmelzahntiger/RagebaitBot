package com.tree3.util;

import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class LaTeXParser {
    public static TeXFormula parseFormula(String formula) throws ParseException {
        return new TeXFormula(formula);
    }

    public static TeXIcon generateIcon(TeXFormula formula) {
        return formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 80);
    }

    public static BufferedImage toImage(TeXIcon icon) {
        BufferedImage img = new BufferedImage(
                icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();
        return img;
    }

    public static BufferedImage formulaToImage(String formula) throws ParseException {
        return toImage(generateIcon(parseFormula(formula)));
    }
    public static Optional<InputStream> bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
            return Optional.of(new ByteArrayInputStream(outputStream.toByteArray()));
        }
        catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }
    public static Optional<InputStream> formulaToInputStream(String formula) {
        return bufferedImageToInputStream(formulaToImage(formula));
    }

}