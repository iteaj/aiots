package com.iteaj.iboot.plugin.edge.server;

import com.iteaj.iboot.plugin.edge.server.network.EdgeServerComponent;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.event.CombinedEventListener;
import org.springframework.stereotype.Component;

@Component
public class EdgeStatusListener implements CombinedEventListener {

    @Override
    public void online(String source, FrameworkComponent component) {
        if(component instanceof EdgeServerComponent) {
            System.out.println(source);
        }
    }

    @Override
    public void offline(String source, FrameworkComponent component) {
        if(component instanceof EdgeServerComponent) {

        }
    }
}
