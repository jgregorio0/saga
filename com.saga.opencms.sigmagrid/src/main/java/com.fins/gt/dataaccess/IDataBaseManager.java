package com.fins.gt.dataaccess;

import java.sql.Connection;

public abstract interface IDataBaseManager
{
  public abstract void setURL(String paramString);

  public abstract void setUser(String paramString);

  public abstract void setPassword(String paramString);

  public abstract void setMaxConnections(int paramInt);

  public abstract void dispose();

  public abstract Connection getConnection();
}

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.IDataBaseManager
 * JD-Core Version:    0.6.0
 */