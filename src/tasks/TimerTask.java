package tasks;

import java.util.Date;
import java.text.DateFormat;
import java.util.Map;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.WorkflowTask;


/**
 * A workflow taks for timer.
 * 
 * @author kenny
 */
public class TimerTask implements WorkflowTask {
    
    
    public static final String IN_TIME = "timer_time";
    
    
    public boolean execute(Activity activity, RunningContext context, Map<String, String> properties) {
        String time = context.getStrValue(IN_TIME);
        
        try {
            Date date = DateFormat.getDateTimeInstance().parse(time);
            //save the timer
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }//execute()
    
    
}//class TimerTask
