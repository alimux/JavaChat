
package dnr2i.util.event;

import java.util.ArrayList;

/**
 *
 * @Alex Ducreux
 */
public abstract class ModelListenable {
    
    private ArrayList<ModelListener> listeners;
    
    public ModelListenable()
    {
        listeners = new ArrayList<>();
    }
    
    public void addModelListener(ModelListener l)
    {
       listeners.add(l);
       l.modelChanged(this);
    }
    public void removeModelListener(ModelListener l)
    {
        listeners.remove(l);
    }
    protected void fireChanged()
    {
        for(ModelListener l:listeners)
        {
            l.modelChanged(this);
        }
    }
    
}
