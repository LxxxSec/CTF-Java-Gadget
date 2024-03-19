package com.xiinnn.tostring2getter;

import com.fasterxml.jackson.databind.node.POJONode;
import com.xiinnn.template.GetterClass;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

// POJONode#toString -> getter
// 实测：jdk8u181 jackson-databind#2.14.1
public class JacksonToString2Getter {
    public static void main(String[] args) throws Exception{
        GetterClass getterClass = new GetterClass();
        // 删除 BaseJsonNode#writeReplace 方法用于顺利序列化
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass0 = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass0.getDeclaredMethod("writeReplace");
        ctClass0.removeMethod(writeReplace);
        ctClass0.toClass();

        POJONode node = new POJONode(getterClass);
        // 成功调用 GetterClass#getName
        node.toString();
    }
}
