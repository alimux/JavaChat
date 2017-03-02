package dnr2i.util.event;

import java.util.ArrayList;

/**
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public abstract class ListenableModel implements IListenableModel
{    
    private final ArrayList<ListenerModel> listeners;
    
    public ListenableModel()
    {
        listeners = new ArrayList<>();
    }
    
    @Override
    public void addModelListener(ListenerModel l)
    {
       listeners.add(l);
       l.modelChanged(this);
    }
    @Override
    public void removeModelListener(ListenerModel l)
    {
        listeners.remove(l);
    }
    protected void fireChanged()
    {
        for(ListenerModel l:listeners) {
            l.modelChanged(this);
        }
    }
    
}
