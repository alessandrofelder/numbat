package numbat;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.table.DefaultColumn;
import org.scijava.table.DefaultGenericTable;
import org.scijava.table.Table;

import java.io.File;
import java.util.ArrayList;

import static net.imglib2.type.numeric.ARGBType.rgba;

/**
 * A very simple ImageJ {@link Command} plugin that calculates the mean and std deviation
 * of an input image, while ignoring any NaN values contained in the image.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Numbat>Stats (ignore NaNs)")
public class IgnoreNanStats<T extends RealType<T>> extends ContextCommand {

    @Parameter
    ImgPlus<T> inputImage;

    @Parameter
    OpService opService;

    /**
     * The results of the command in a {@link Table}.
     */
    @Parameter(type = ItemIO.OUTPUT, label = "Numbat results")
    protected Table<DefaultColumn<Double>, Double> resultsTable;

    @Override
    public void run() {
        ArrayList<DoubleType> nonNanValues = new ArrayList<>();
        Cursor<T> cursor = inputImage.cursor();
        while(cursor.hasNext())
        {
            cursor.fwd();
            if(!(Double.isNaN(cursor.get().getRealDouble())))
            {
                nonNanValues.add(new DoubleType(cursor.get().getRealDouble()));
            }
        }

        DoubleType mean = opService.stats().mean(nonNanValues);
        DoubleType stdDev = opService.stats().stdDev(nonNanValues);

        resultsTable = (Table) new DefaultGenericTable();
        resultsTable.appendColumn("3D Mean (ignoring NaNs)");
        resultsTable.appendColumn("3D Std Dev (ignoring NaNs)");
        resultsTable.appendRow(inputImage.getName());
        resultsTable.set(0,0,mean.getRealDouble());
        resultsTable.set(1,0, stdDev.getRealDouble());
    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        // ask the user for a file to open
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            // load the dataset
            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

            // show the image
            ij.ui().show(dataset);

            // invoke the plugin
            ij.command().run(IgnoreNanStats.class, true);
        }
    }

    private void addValueAtPosition(Img<DoubleType> image, double valueToAdd, long[] position) {
        final RandomAccess<DoubleType> access = image.randomAccess();
        access.setPosition(position);
        double currentValue = access.get().getRealDouble();
        access.get().setReal(currentValue+valueToAdd);
    }

}
