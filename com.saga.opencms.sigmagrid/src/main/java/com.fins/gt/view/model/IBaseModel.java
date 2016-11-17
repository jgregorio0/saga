package com.fins.gt.view.model;

import java.io.Writer;

public abstract interface IBaseModel
{
  public abstract String toHTML();

  public abstract void outHTML(Writer paramWriter);

  public abstract void setProperty(String paramString, Object paramObject);

  public abstract Object getProperty(String paramString);
}

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.view.model.IBaseModel
 * JD-Core Version:    0.6.0
 */