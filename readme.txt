利用反射机制完成ClientHandler 根据uri动态加载对应Servlet并调用其service方法执行业务操作

问题：之前我们在ClientHandler中判断uri的值是否应对一个业务时，添加了很多分支。这样做由一个弊端：
每当我们添加一个新业务时，都要修改ClientHandle，添加新的分支，将uri与该Servlet对应。

解决办法：
让ClientHandler得到一个uri后，查看该uri是否对应一个Servlet如果对应，得到该Serclet名字并动态加载
然后实例化。之后调用其service方法。
而uri是否对应某个Servlet我们可以利用一个Map来维护，其中key为uri的值，value位对应Servlet的名字。
并且这些对应关系可以用一个xml文件来维护。那么每当我们多一个新业务时，我们只需要在该xml文件中添加一个新
的uri与Servlet的对应关系即可。这样一来，ClientHandler就无需在进行	修改。