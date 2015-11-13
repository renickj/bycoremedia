package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.user.User;

import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getValueForKey;

public class UserImpl extends AbstractIbmCommerceBean implements User {

  private Map<String, Object> delegate;

  public UserImpl(Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  public UserImpl() {
    //mandatory default constructor for bean creation
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatPersonId(getExternalId());
  }

  public Map<String, Object> getDelegate() {
    return delegate;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  @Override
  public String getFirstName() {
    return getValueForKey(getDelegate(), "firstName", String.class);
  }

  @Override
  public String getLastName() {
    return getValueForKey(getDelegate(), "lastName", String.class);
  }

  @Override
  public String getLogonId() {
    return getValueForKey(getDelegate(), "logonId", String.class);
  }

  @Override
  public String getUserId() {
    return getValueForKey(getDelegate(), "userId", String.class);
  }

  @Override
  public String getExternalId() {
    return getValueForKey(getDelegate(), "logonId", String.class);
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  @Override
  public void setFirstName(String name) {
    getDelegate().put("firstName", name);
  }

  @Override
  public void setLastName(String name) {
    getDelegate().put("lastName", name);
  }

  @Override
  public void setLogonId(String id) {
    getDelegate().put("logonId", id);
  }

  @Override
  public String getEmail1() {
    return getValueForKey(getDelegate(), "email1", String.class);
  }

  @Override
  public void setEmail1(String mail) {
    getDelegate().put("email1", mail);
  }

  @Override
  public String getEmail2() {
    return getValueForKey(getDelegate(), "email2", String.class);
  }

  @Override
  public void setEmail2(String mail) {
    getDelegate().put("email2", mail);
  }

  @Override
  public String getEmail3() {
    return getValueForKey(getDelegate(), "email3", String.class);
  }

  @Override
  public void setEmail3(String mail) {
    getDelegate().put("email3", mail);
  }

  @Override
  public String getCity() {
    return getValueForKey(getDelegate(), "city", String.class);
  }

  @Override
  public void setCity(String city) {
    getDelegate().put("city", city);
  }

  @Override
  public String getCountry() {
    return getValueForKey(getDelegate(), "country", String.class);
  }

  @Override
  public void setCountry(String country) {
    getDelegate().put("country", country);
  }

  @Override
  public String getLogonPassword() {
    return getValueForKey(getDelegate(), "logonPassword", String.class);
  }

  @Override
  public void setLogonPassword(String password) {
    getDelegate().put("logonPassword", password);
  }

  @Override
  public String getLogonPasswordVerify() {
    return getValueForKey(getDelegate(), "logonPasswordVerify", String.class);
  }

  @Override
  public void setLogonPasswordVerify(String password) {
    getDelegate().put("logonPasswordVerify", password);
  }

  @Override
  public void setChallengeAnswer(String challengeAnswer) {
    getDelegate().put("challengeAnswer", challengeAnswer);
  }

  @Override
  public String getChallengeAnswer() {
    return getValueForKey(getDelegate(), "challengeAnswer", String.class);
  }

  @Override
  public void setChallengeQuestion(String question) {
    getDelegate().put("challengeQuestion", question);
  }

  @Override
  public String getChallengeQuestion() {
    return getValueForKey(getDelegate(), "challengeQuestion", String.class);
  }

  @Override
  public void setPasswordExpired(boolean b) {
    getDelegate().put("passwordExpired", b);
  }

  @Override
  public boolean isPasswordExpired() {
    return Boolean.valueOf(getValueForKey(getDelegate(), "passwordExpired", String.class));
  }
}
