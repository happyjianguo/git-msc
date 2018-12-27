/***************************************************/
/******************1、初始化插件********************/
/***************************************************/
var isIE = navigator.userAgent.toLowerCase().search(/(msie\s|trident.*rv:)([\w.]+)/) != -1;
/*加载北京CA网页插件*/
try {
    if (isIE) {
        document.writeln("<OBJECT classid=\"CLSID:24B16510-62B4-40FD-A3BD-EE19914CDB0F\" height=1 id=XTXAPP style=\"HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px\" width=1 VIEWASTEXT>");
        document.writeln("</OBJECT>");
    }
    else {
        document.writeln("<embed id=XTXAPP1 type=application/x-xtx-axhost clsid={24B16510-62B4-40FD-A3BD-EE19914CDB0F} width=1 height=1 />");
    }
} catch (e) {
    alert("证书环境安装失败！");
}

try {
    if (isIE) {
        document.writeln("<OBJECT classid=\"CLSID:3F367B74-92D9-4C5E-AB93-234F8A91D5E6\" height=1 id=XTXAPP1 style=\"HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px\" width=1 VIEWASTEXT>");
        document.writeln("</OBJECT>");
    }
    else {
        document.writeln("<embed id=XTXAPP1 type=application/x-xtx-axhost clsid={3F367B74-92D9-4C5E-AB93-234F8A91D5E6} width=1 height=1 />");
    }
} catch (e) {

}




/*加载网证通网页插件*/
var initialObj;
try {
    initialObj = new ActiveXObject("NetcaPki.Utilities");
} catch (e) {
    initialObj = null;
}

/***************************************************/
/******************2、主要对外调用方法******************/
/***************************************************/

/**
 * 2.1、获取用户列表 
 * @returns {} 返回结果：返回Array对象,结果为NAME,SN
 * 用户名1|证书SN码1
 * 用户名2|证书SN码2
 * 用户名3|证书SN码3............
 */
function GetUserList() {
    var userList = "";
    try {
        userList += B_GetUserList();
    } catch (e) {
    }
    try {
        userList += W_GetUserList();
    } catch (e) {
    }
    return RemovCa(userList.toUpperCase());
}

/**
 * 2.2、获取Base64位证书
 * @param {} sn 通过2.1、GetUserList方法获取到证书的SN码
 * @returns {} 返回Base64位证书值
 */
function GetUserCert(sn) {
    var type1 = JudCaType1(sn);
    var cert;
    switch (type1) {
        case 1:
            cert = W_GetUserCert(sn);
            break;
        case 2:
            cert = B_GetUserCert(sn);
            break;
        default:
            cert = null;
    }
    return cert;
}

/**
 * 2.3、获取证书唯一值，默认获取深圳地标，如果返回空表示获取失败
 * @param {} sn 证书SN码
 * @returns {} 返回证书的唯一值
 */
function GetCertId(sn) {
    var type1 = JudCaType1(sn);
    var id;
    switch (type1) {
        case 1:
            id = W_GetCertId(sn);
            break;
        case 2:
            id = B_GetCertId(sn);
            break;
        default:
            id = null;
    }
    return id;
}

/**
 * 2.4、数据签名
 * @param {} sn 证书SN码
 * @param {} indata 需要签名的数据
 * @returns {} 返回签名结果
 */
function SignData(sn, indata) {
    var type1 = JudCaType1(sn);
    var id;
    try {
        switch (type1) {
            case 1:
                id = W_SignData(sn, indata);
                break;
            case 2:
                id = B_SignData(sn, indata);
                break;
            default:
                id = null;
        }
    } catch (e) {
        id = null;
    }
    return id;
}

/**
 * 2.5、验证签名
 * @param {} sn 证书SN码
 * @param {} indata 需要签名的数据
 * @param {} signdata 签名结果
 * @returns {} 返回验证结果
 */
function VerSignData(sn, indata, signdata) {
    var type1 = JudCaType1(sn);
    var id;
    switch (type1) {
        case 1:
            id = W_VerSignData(sn, indata, signdata);
            break;
        case 2:
            id = B_VerSignData(sn, indata, signdata);
            break;
        default:
            id = false;
    }
    return id;
}


/**
 * 2.6、给予药品交易平台能够直接获取到证书的绑定值
 * @returns {} 
 */
function GetKeyToMsc() {

    var keys = GetUserList();
    if (keys.length == 0 || keys == null) {
        alert("请插入需要进行数据绑定的证书！");
        return "";
    }

    if (keys.length > 1) {
        alert("检测到插入多个证书，请只留下需要绑定的证书，把其他证书移除！");
        return "";
    }

    var sn = keys[0].sn;

    var val = GetCertId(sn);
    if (val == "" || val == null) {
        alert("证书绑定值获取失败，请重新获取！");
        return "";
    } else {
        return val;
    }

}

/***************************************************/
/******************3、证书执行方法******************/
/***************************************************/

//GetUserList
function B_GetUserList() {
    //if (XTXAPP != null) {
    var userList = "";
    var strUserList = "";
    try { strUserList = XTXAPP1.SOF_GetUserList(); } catch (e) { }
    if (strUserList == "" || strUserList == null) {
        try { strUserList = XTXAPP.GetUserList(); } catch (e) { }
    }

    if (strUserList != "" || strUserList != null) {
        var array = strUserList.split("&");
        for (var i = 0; i < array.length; i++) {
            if (array[i] !== "") {
                var a = array[i].split("|");
                userList += a[0] + "|" + a[2] + "|" + 2 + ";";
            }
        }
        return userList;
    }
    return "";
}

function W_GetUserList() {

    if (initialObj) {
        var userList = "";
        var certs = W_GetCerts();
        for (var i = 0; i < certs.length; i++) {
            var oCert = certs[i];
            if (oCert !== null) {
                userList += oCert.SubjectCN + "|" + initialObj.BinaryToHex(oCert.ThumbPrint(8192), true) + "|" + 1 + ";";
            }
        }
        return userList;
    } else {
        return "";
    }
}


//GetUserCert
function B_GetUserCert(sn) {

    var userCert = null;
	try { userCert = XTXAPP1.SOF_ExportUserCert(sn.toLowerCase()); } catch (e) { userCert = null; }
    if (userCert == null || userCert == "")
		try { userCert = XTXAPP.ExportUserCert(sn.toLowerCase()); } catch (e) { userCert = null; }
    return userCert;

}

function W_GetUserCert(sn) {
    if (initialObj) {
        var oCerts = W_GetCerts();
        try {
            if (oCerts != null) {
                for (var i = 0; i < oCerts.length; i++) {
                    var oCert = oCerts[i];
                    var k = initialObj.BinaryToHex(oCert.ThumbPrint(8192), true);
                    if (k.toUpperCase() == sn.toUpperCase()) {
                        var certPem = oCert.Encode(3);
                        return certPem;
                    }
                }
            }
        } catch (e) {
            return null;
        }
    } else {
        return null;
    }
}

//GetCertId
function B_GetCertId(sn) {
    if (XTXAPP) {
        var cert = B_GetUserCert(sn);
        if (cert != null) {
            var sid = "";
            try {
                if (sid == "")
                    sid = XTXAPP.GetUserInfoByOid(cert, "1.2.86.11.7.1");
                if (sid == "")
                    sid = XTXAPP.GetUserInfoByOid(cert, "1.2.86.11.7.8");
                if (sid == "")
                    sid = XTXAPP.GetUserInfoByOid(cert, "2.16.840.1.113732.2");
                if (sid == "")
                    sid = XTXAPP.GetUserInfoByOid(cert, "1.2.156.112562.2.1.1.4");
            } catch (e) {
                sid = "";
            }
            try {
                if (sid == "")
                    sid = XTXAPP1.SOF_GetCertInfoByOid(cert, "1.2.86.11.7.1");
                if (sid == "")
                    sid = XTXAPP1.SOF_GetCertInfoByOid(cert, "1.2.86.11.7.8");
                if (sid == "")
                    sid = XTXAPP1.SOF_GetCertInfoByOid(cert, "2.16.840.1.113732.2");
                if (sid == "")
                    sid = XTXAPP1.SOF_GetCertInfoByOid(cert, "1.2.156.112562.2.1.1.4");
            } catch (e) {
                sid = "";
            }
            return sid;
        } else {
            return "";
        }
    } else {
        return "";
    }
}

function W_GetCertId(sn) {
    if (initialObj) {
        var cert = W_GetUserCert(sn);
        var certObj = initialObj.CreateCertificateObject();
        certObj.Decode(cert);
        var sid = "";
        // sid = GetExtension(cert, "1.3.6.1.4.1.18760.1.12.11");
        if (sid == "" || sid == null)
            sid = initialObj.BinaryToHex(certObj.ThumbPrint(8192), true); //获取姆印

        return sid;
    } else {
        return "";
    }
}

//SignData
function B_SignData(sn, indata) {

    var data = "";
    try { data = XTXAPP.SignedData(sn.toLowerCase(), indata); } catch (e) { }
    if (data == "" || data == null) {
        try { data = XTXAPP1.SOF_SignData(sn.toLowerCase(), indata); } catch (e) { }
    }

    return data;
}

function W_SignData(sn, indata) {
    var cert = W_GetUserCert(sn);
    var oCert = initialObj.CreateCertificateObject();
    oCert.Decode(cert);

    var tbs = null;
    if (typeof (indata) == "string" || typeof (indata) == "String") {
        tbs = initialObj.Encode(indata, 65001);
    } else {
        tbs = indata;
    }


    var signObj = initialObj.CreateSignedDataObject();
    signObj.SetSignCertificate(oCert, "", true);
    signObj.SetSignAlgorithm(-1, oCert.PublicKeyAlgorithm == "1" ? 2 : 25);
    signObj.SetIncludeCertificateOption(2);
    signObj.Detached = false;

    var str = signObj.Sign(tbs, 2);
    signObj = null;

    return str;

    //var arrRT = oCert.Sign(oCert.PublicKeyAlgorithm == "1" ? 2 : 25, tbs);
    //return initialObj.Base64Encode(arrRT, 1);


}

//VerSignData
function B_VerSignData(sn, indata, signdata) {
    var data = "";
    data = XTXAPP.VerifySignedData(sn.toLowerCase(), indata, signdata);
    if (data == "" || data == null)
        try {
            var cert = B_GetUserCert(sn);
            data = XTXAPP1.SOF_VerifySignedData(cert, indata, signdata);
        } catch (e) {

        }

    return data;
}

function W_VerSignData(sn, indata, signdata) {
    //var cert = W_GetUserCert(sn);
    //var oCert = initialObj.CreateCertificateObject();
    //oCert.Decode(cert);
    //var tbs = null;
    //if (typeof (indata) == "string" || typeof (indata) == "String") {
    //    tbs = convertByte(indata);
    //} else {
    //    tbs = indata;
    //}
    //var bSignature = base64Decode(signdata);
    //if (bSignature !== "") {
    //    return oCert.Verify(oCert.PublicKeyAlgorithm == "1" ? 2 : 25, tbs, bSignature);
    //}
    //else {
    //    return false;
    //}


    var signObj = initialObj.CreateSignedDataObject();
    var bSource;
    if (typeof (indata) == "string" || typeof (indata) == "String") { // 原文是字符串
        bSource = convertByte(indata);
    } else { // 原文是数组
        bSource = indata;
    }
    var bSignature = base64Decode(signdata);

    var checkSignFormat = signObj.IsSign(bSignature);
    if (!checkSignFormat) {
        return false;
    }
    var isDetached = signObj.IsDetachedSign(bSignature);
    if (isDetached) /**不带原文的签名 ***/ {
        var tbs = signObj.DetachedVerify(bSource, bSignature, false);
        if (!tbs) {
            return false;
        }
    } else {
        var tbs = signObj.Verify(bSignature, true);
        var isOK = initialObj.ByteArrayCompare(tbs, bSource);
        if (!isOK) {
            return false;
        }
    }
    // var certObj = signObj.GetSignCertificate(-1);
    return true;
}


/***************************************************/
/******************4、公用方法******************/
/***************************************************/

/**
 * 判断属于哪家证书
 * @param {} sn 证书SN码
 * @returns {} 返回证书类型，0判断失败、1网证通、2北京CA
 */
function JudCaType1(sn) {
    if (W_GetUserCert(sn) != null) {
        return 1;
    }
    if (B_GetUserCert(sn) != null) {
        return 2;
    }
}

/**
 * 获取网证通所有证书
 * @returns {} 符合条件的所有网证通证书
 */
function W_GetCerts() {
    var storeObj;
    var arrlist = new Array();
    if (initialObj) {
        try {
            storeObj = initialObj.CreateStoreObject();
        } catch (e) {
            return null;
        }
        storeObj.Open(0, "my");

        for (var i = 1; i < storeObj.GetCertificateCount() + 1; i++) {
            var oCert = storeObj.GetCertificate(i);
            if (oCert !== null) {
                var ind = oCert.Issuer.indexOf("NETCA");
                if (!((oCert.KeyUsage == (1 | 2)) || (oCert.KeyUsage == -1) || (oCert.KeyUsage == 1)) || (ind < 0))
                    continue;
                arrlist.push(oCert);
            }
        }
        storeObj.Close();
        storeObj = null;
    }
    return arrlist;
}

/**
 * 取出重复数据
 * @param {} userList 用户列表
 * @returns {} 返回去除重复后的结果
 */
function RemovCa(userList) {
    var ArrayUser = new Array();
    var hash = {};
    var lis = userList.split(";");

    for (var i = 0, elem; (elem = lis[i]) != null; i++) {
        if (!hash[elem]) {
            if (elem != "") {
                var a = elem.split("|");
                var arr = { "sn": a[1], "name": a[0],"type":a[2] };
                ArrayUser.push(arr);
            }
            hash[elem] = true;
        }
    }
    return ArrayUser;
}

/**
 * 获取指定的OID的值
 * @param {} cert Base64位证书
 * @param {} oid OID
 * @returns {} OID对应的值
 */
function GetExtension(cert, oid) {
    var certObj = initialObj.CreateCertificateObject();
    certObj.Decode(cert);
    try {
        var sid = initialObj.DecodeASN1String(1, certObj.GetExtension(oid));
        return sid;
    } catch (e) {
        return "";
    }
}


/**
 * 字符串转byte[]
 * @param {} sSource 
 * @returns {} 
 */
function convertByte(sSource) {
    try {
        return initialObj.Encode(sSource, 65001);
    } catch (e) {
        return null;
    }
}

/**
 * byte[]转字符串
 * @param {} bDatas 
 * @returns {} 
 */
function convertString(bDatas) {
    try {
        return initialObj.Decode(bDatas, 65001);
    } catch (e) {
        return "";
    }
}

/**
 * Base64编码 
 * @param {} sSource 原文
 * @returns {} BASE64信息
 */
function base64Encode(sSource) {
    try {
        return initialObj.Base64Encode(sSource, 1);
    } catch (e) {
        return null;
    }
}

/**
 * Base64解码 
 * @param {} strBase64 strBase64
 * @returns {} 原文
 */
function base64Decode(strBase64) {
    try {
        return initialObj.Base64Decode(strBase64, 2);
    } catch (e) {
        return "";
    }
}


function GetBjcaCertID(sn) {
    if (XTXAPP) {
        return XTXAPP.GetUserInfoByOid(sn, "1.2.86.11.7.1");
    } else {
    	return "";
    }
}
