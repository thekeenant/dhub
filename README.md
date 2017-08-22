# dhub

Device Hub is an internet-of-things REST API that allows communication with various connected
devices from a centralized server. Through enabling different plugins, devices of
different communication protocols can be connected to the platform, and then manipulated
via the REST interface.

Currently the REST interface is not complete, but the plugin for communicating
with Z-Wave enabled devices supports many commands and can be used reliably with modern
Z-Wave devices.

## Z-Wave

Your Z-Wave USB controller must be connected to the same machine of the server because
it will communicate over the serial port.

**Controller:** Provide the name of the serial port and start communication. Multiple
controllers are supported as controllers are completely isolated.

```java
Controller = controller = new Controller("ttyACM0");
controller.start() // open the port, enable communication
```

**Setting:** The most basic test of a Z-Wave device is the basic set command.
```java
// set the node with id of 1 to 50%
controller.send(new SendDataMsg<>(1, CmdClass.BASIC.setPercent(0.5)));

// turn off
controller.send(new SendDataMsg<>(1, CmdClass.BASIC.setOff()));
```

**Get/Reports:** We can request updates to a node and fetch their current status.
The message system leverages mbassador as the event bus within the controller object.

```java
// first we must listen to the report back with an async handler
controller.listen(BasicReportEvent.class, (listener, event) -> {
    if (event.getNodeId() == 1) {
        double percent = event.getCmd().getPercent();
        // ...
    }
});

// then send the request
controller.send(new SendDataMsg<>(1, CmdClass.BASIC.get()));
```

**Multichannel support:** Some devices have multiple endpoints, such as a power outlet
with 4 separate AC outlets that can be controlled individually. Here, we set the
3rd subnode of the node with id 1 to 25%.

```java
Encap<InboundCmd> encap = CmdClass.MULTI_CHANNEL.encap(3, CmdClass.BASIC.setPercent(0.25));
controller.send(new SendDataMsg<>(1, cmd)).await();
```

**Other commands:** More can be found in the source. Here we request the controller
a list of nodes:

```java
// first we register and async handler for a future reply message
controller.listen(NodeListReplyEvent.class, (listener, event) -> {
    Set<Integer> nodeIds = event.getMessage().getNodeIds();
    // handle node ids accordingly
});
// then send the request message
controller.send(new NodeListMsg());
```

More examples can be found in the examples folder.

## Contribute

I welcome any contributions, and the source code is licensed under the
highly permissive MIT license.

**Resources:**

* [Z-Wave Public Spec](http://z-wave.sigmadesigns.com/resources/)