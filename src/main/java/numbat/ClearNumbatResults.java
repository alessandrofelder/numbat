package numbat;

import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.table.DefaultGenericTable;
import org.scijava.table.Table;

import java.util.List;

@Plugin(type = Command.class, menuPath = "Plugins>Numbat>Clear Numbat results")
public class ClearNumbatResults extends ContextCommand {
    @Parameter
    private DisplayService displayService;

    @Override
    public void run() {
        final List<Display<?>> displays = displayService.getDisplays(IgnoreNanStats.resultsTable);
        displays.forEach(Display::close);
        IgnoreNanStats.resultsTable =  (Table) new DefaultGenericTable();
    }
}
