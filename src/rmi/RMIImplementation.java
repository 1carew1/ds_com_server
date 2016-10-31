package rmi;
/***************************************************************
 * Copyright (c) 2016 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Colm Carew on 31/10/2016.
 */
public class RMIImplementation extends UnicastRemoteObject implements RMIInterface {


    public RMIImplementation() throws RemoteException {

    }

    @Override
    public String obtainString(String value) throws RemoteException {
        return value.replaceAll("\\s+", " ").toUpperCase();
    }

    @Override
    public ComplextRMIObject createWithId(Long id) throws RemoteException {
        return new ComplextRMIObject(id, "Some Name", 123456789);
    }
}