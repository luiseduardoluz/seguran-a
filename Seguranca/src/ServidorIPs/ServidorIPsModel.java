/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorIPs;

import java.util.Hashtable;
/**
 *
 * @author LuisEduardo
 */
public class ServidorIPsModel {
    
    Hashtable<String, String> ht = new Hashtable<String, String>();

    public Hashtable<String, String> getHt() {
        return ht;
    }

    public void setHt(Hashtable<String, String> ht) {
        this.ht = ht;
    }


    public ServidorIPsModel(){}
    
}