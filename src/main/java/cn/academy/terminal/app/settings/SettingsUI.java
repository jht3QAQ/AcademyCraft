package cn.academy.terminal.app.settings;

import cn.academy.core.AcademyCraft;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.WidgetContainer;
import cn.lambdalib2.cgui.component.ElementList;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.cgui.component.VerticalDragBar;
import cn.lambdalib2.cgui.component.VerticalDragBar.DraggedEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class SettingsUI extends CGuiScreen {
    
    static WidgetContainer document;
    
    private static Map<String, List<UIProperty>> properties = new HashMap<>();
    static {
        addProperty(PropertyElements.CHECKBOX, "generic", "attackPlayer", true, true);
        addProperty(PropertyElements.CHECKBOX, "generic", "destroyBlocks", true, true);
        addProperty(PropertyElements.CHECKBOX, "generic", "headsOrTails", false, false);
        addProperty(PropertyElements.CHECKBOX, "generic", "useMouseWheel", false, false);
    }

    @RegInitCallback
    private static void __init() {
        document = CGUIDocument.read(new ResourceLocation("academy:guis/settings.xml"));
    }
    
    public static void addProperty(IPropertyElement elem, String cat, String id, Object defValue, boolean singlePlayer) {
        add(cat, new UIProperty.Config(elem, cat, id, defValue, singlePlayer));
    }

    public static void addCallback(String id, String cat, Runnable callback, boolean singlePlayer) {
        add(cat, new UIProperty.Callback(PropertyElements.CALLBACK, id, callback, singlePlayer));
    }

    private static void add(String cat, UIProperty prop) {
        List<UIProperty> list = properties.get(cat);
        if(list == null)
            properties.put(cat, list = new ArrayList<>());
        list.add(prop);
    }
    
    public SettingsUI() {
        initPages();
    }

    @Override
    public void onGuiClosed() {
        AcademyCraft.config.save();
        super.onGuiClosed();
    }

    private void initPages() {
        Widget main = document.getWidget("main").copy();
        
        Widget area = main.getWidget("area");
        
        boolean singlePlayer = Minecraft.getMinecraft().isSingleplayer();
        
        ElementList list = new ElementList(); 
        {
            for(Entry<String, List<UIProperty>> entry : properties.entrySet()) {
                Widget head = document.getWidget("t_cathead").copy();
                TextBox.get(head.getWidget("text")).setContent(local("cat." + entry.getKey()));
                list.addWidget(head);
                
                for(UIProperty prop : entry.getValue()) {
                    if(!prop.singlePlayer || singlePlayer)
                        list.addWidget(prop.element.getWidget(prop));
                }
                
                Widget placeholder = new Widget();
                placeholder.transform.setSize(10, 20);
                list.addWidget(placeholder);
            }
        } 
        area.addComponent(list);
        
        Widget bar = main.getWidget("scrollbar");
        bar.listen(DraggedEvent.class, (w, e) ->
        {
            list.setProgress((int) (list.getMaxProgress() * VerticalDragBar.get(w).getProgress()));
        });
        
        gui.addWidget(main);
    }
    
    private String local(String id) {
        return StatCollector.translateToLocal("ac.settings." + id);
    }
    
}