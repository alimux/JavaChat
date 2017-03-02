package dnr2i.util.event;

/**
 * Interface Listenable model
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public interface IListenableModel 
{
    /**
     * Add subsription to listener
     * @param l 
     */ 
    void addModelListener(ListenerModel l);
    
    /**
     * remove subscription
     * @param l 
     */
    void removeModelListener(ListenerModel l);

}
