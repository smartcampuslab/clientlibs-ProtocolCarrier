********************
* Protocol Carrier *
********************

Use this project as library.

Create an instance of Protocol Carrier using application context and application token as arguments:
    ProtocolCarrier pc = new ProtocolCarrier(getApplicationContext(), "appToken");

Available methods (cfr. document):
    start(String appToken), stop(String appToken), clear(String appToken),
    getPendingMessages(String appToken), removeMessage(String appToken, String messageId)