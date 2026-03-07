1.总是使用**中文**回复

2.**不要**写注释, **不要**写javadoc, 除非代码在实现数学公式

3.除非说明了不要写测试, 记得写测试或者修改功能描述的对应测试, 你不要主动使用RunCommand来运行测试

4.**不要**修改README.md

5.**不要**写文档, **不要**写implementation_summary

6.从不 import static 或者在 import 使用 * 通配符

7.为widget写测试时, 创建一个新类和主方法,使用WidgetTestFrame, 不需要添加test注解 

8.不要在方法上throws任何unchecked的异常, 如果没有好的处理手段则catch, logger记录并throw