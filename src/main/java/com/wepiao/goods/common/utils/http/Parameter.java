/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.wepiao.goods.common.utils.http;

public class Parameter implements java.io.Serializable {
  String name;
  String value;

  private static final long serialVersionUID = -8708108746980739212L;

  public Parameter(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public Parameter(String name, long value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  public Parameter(String name, double value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  public Parameter(String name, int value) {
    this.name = name;
    this.value = String.valueOf(value);
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  private static final String JPEG = "image/jpeg";
  private static final String GIF = "image/gif";
  private static final String PNG = "image/png";
  private static final String OCTET = "application/octet-stream";

  public static Parameter[] getParameterArray(String name, String value) {
    return new Parameter[] {new Parameter(name, value)};
  }

  public static Parameter[] getParameterArray(String name, int value) {
    return getParameterArray(name, String.valueOf(value));
  }

  public static Parameter[] getParameterArray(String name1, String value1
      , String name2, String value2) {
    return new Parameter[] {new Parameter(name1, value1)
        , new Parameter(name2, value2)};
  }

  public static Parameter[] getParameterArray(String name1, int value1
      , String name2, int value2) {
    return getParameterArray(name1, String.valueOf(value1), name2, String.valueOf(value2));
  }

  @Override public String toString() {
    return "Parameter{" +
        "name='" + name + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Parameter parameter = (Parameter) o;

    if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
    return !(value != null ? !value.equals(parameter.value) : parameter.value != null);
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
