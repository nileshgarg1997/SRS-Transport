package com.myapp.srstransport;

public class LoginData {

    private String registeredMobileNo,routeNo;

    public LoginData() {
    }

    public LoginData(String registeredMobileNo, String routeNo) {
        this.registeredMobileNo = registeredMobileNo;
        this.routeNo = routeNo;
    }

    public String getRegisteredMobileNo() {
        return registeredMobileNo;
    }

    public void setRegisteredMobileNo(String registeredMobileNo) {
        this.registeredMobileNo = registeredMobileNo;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }
}
