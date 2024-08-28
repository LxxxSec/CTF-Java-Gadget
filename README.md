# CTF-Java-Gadget

## 前言

CTF-Java-Gadget专注于收集CTF中Java赛题的反序列化片段，项目地址：

- https://github.com/LxxxSec/CTF-Java-Gadget

如有新链（或片段）想要添加至 CTF-Java-Gadget 项目，可提交至issue！

## 使用方法

参考 [CTF-Java-Gadget.pdf](./CTF-Java-Gadget.pdf)

## ChangeLog

20240828

-   [x] 添加 HashMap#readObject -> UIDefaults$TextAndMnemonicHashMap -> toString 链

20240826

-   [x] 添加 CodeSigner#toString -> exec 链（来源：https://www.n1ght.cn/2024/04/17/java%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E6%BC%8F%E6%B4%9Ecommons-collections-TransformedList%E8%A7%A6%E5%8F%91transform/）

20240812

-   [x] 添加 JtaTransactionManager#readObject -> JNDI 链

20240319

-   [x] 公开 CTF-Java-Gadget 项目
