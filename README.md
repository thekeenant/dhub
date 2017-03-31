# dhub 

[![Build Status](https://travis-ci.com/thekeenant/dhub.svg?token=JyCLGy14nEunKyYpGw9c&branch=master)](https://travis-ci.com/thekeenant/dhub)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e18343861f5541beb53c66314038efc0)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=thekeenant/dhub&amp;utm_campaign=Badge_Grade)

At JRiver, I have been working on Z-Wave home automation software. This is not it. I've been developing something of my own so that I can learn how Z-Wave works.

## Examples
For examples, see the [examples module](https://github.com/thekeenant/dhub/tree/master/examples/src/main/java/com/keenant/dhub/examples).

### Overview

```java
public class Overview {
    public static void main(String[] args) {    
        Controller controller = new Controller("ttyACM0");
        
        // Set something to max level (bulb, switch, etc.)
        controller.send(new SendDataMsg(1, CmdClass.BASIC.setMax()));
        
        // Listen to an event...
        controller.listen(BasicReportEvent.class, (listener, event) -> {
            int node = event.getNodeId();
            int level = event.getCmd().getValue();

            System.out.println("Node #" + node + " = " + level + "%");
        });
        
        // Request the event that we are expecting by calling this get command...
        controller.send(new SendDataMsg(1, CmdClass.BASIC.get()));
    }
}
```

## Todo
* Resend failed transactions (transaction options/configuration).