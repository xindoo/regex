# regex


最近学习编译原理，基于NFA实现了正则表达式，代码刚写完，具体内容参考博客[https://blog.csdn.net/xindoo/article/details/105875239](https://blog.csdn.net/xindoo/article/details/105875239)，欢迎查阅。  
已实现NFA转DFA，详见博客[从0到1打造正则表达式执行引擎(二)](https://xindoo.blog.csdn.net/article/details/106458165).  

目前还是demo，算是刚把引擎的骨架搭建起来，后续继续完善代码。  

## 是什么不是什么？ 
这个引擎不会是一个可以用在生产环境的项目，但会是一个了解正则引擎背后工作原理的项目。     

## 现状
目前支持的语义   
基本语义: . ? * + () |  
字符集合: [] 
非打印字符: \d \D \s \S \w \W  
支持DFA和NFA双引擎 

## Todo 
- [ ] 支持`{}`限定符     
- [ ] 支持 `^ $ \b` 等定位符   
- [x] 实现DFA引擎
- [ ] DFA最小化(Hopcroft算法)   
- [ ] 支持捕获和引用   