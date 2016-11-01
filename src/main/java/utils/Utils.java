package utils;
/***************************************************************
 * Copyright (c) 2016 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import rmi.ComplextRMIObject;

/**
 * Created by Colm Carew on 01/11/2016.
 */
public class Utils {

    public static ComplextRMIObject createComplextObj() {
        return new ComplextRMIObject(1, "some name", 1234);
    }
}
