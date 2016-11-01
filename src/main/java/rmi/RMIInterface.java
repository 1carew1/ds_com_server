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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Colm Carew on 31/10/2016.
 */
public interface RMIInterface extends Remote {
    public String obtainString(String value) throws RemoteException;

    public ComplextRMIObject createWithId(Long id) throws RemoteException;
}
