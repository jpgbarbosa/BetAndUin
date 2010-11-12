package chatJSP;

import java.util.List;
import java.util.Vector;

public class MessageBean
{
    private List<String> _messages;

    public MessageBean()
    {
	_messages = new Vector<String>();
    }

    public void setMessage(String message)
    {
	_messages.add(message);
    }

    public List<String> getMessages()
    {
	return _messages;
    }

}
