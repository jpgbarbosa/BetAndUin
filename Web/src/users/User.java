package users;

import java.util.Date;

public class User
{
  private String _userName;  
    
  private String _loginDate;
  
  public User(String username)
  {
	  _userName = username;
    _loginDate = (new Date()).toString();
  }

  public void setUserName(String userName)
  {
    _userName = userName;
  }
    
  public String getUserName()
  {
    return _userName;
  }      
  
  public String getLoginDate()
  {
    return _loginDate;
  }
    
}