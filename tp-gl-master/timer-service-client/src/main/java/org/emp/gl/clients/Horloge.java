package org.emp.gl.clients ; 

import org.emp.gl.timer.service.TimerChangeListener;
import org.emp.gl.timer.service.TimerService ; 


public class Horloge implements TimerChangeListener {

    String name; 
    TimerService timerService ; 


    public Horloge (String name, TimerService timerService) {
        this.name = name ; 
        this.timerService = timerService;
        
        // Register this horloge as a listener
        if (timerService != null) {
            timerService.addTimeChangeListener(this);
        }

        System.out.println ("Horloge "+name+" initialized!") ;
    }

    public void afficherHeure () {
        if (timerService != null)
            System.out.println (name + " affiche " + 
                                String.format("%02d:%02d:%02d.%d",
                                timerService.getHeures(),
                                timerService.getMinutes(),
                                timerService.getSecondes(),
                                timerService.getDixiemeDeSeconde())) ;
    }

    @Override
    public void propertyChange(String prop, Object oldValue, Object newValue) {
        // Update display when time changes
        afficherHeure();
    }

    public void disconnect() {
        if (timerService != null) {
            timerService.removeTimeChangeListener(this);
        }
    }
}
