#### 需求来源

你有没有遇到这种问题？
		如果需要去采购某种商品，给定该商品指定规格的包裹及价格，遵循包裹越大均价越便宜的原则，给出一个订单额，需要求出满足该订单（实际采购到的数量不小于订单额）的最优采购方式（输出：总价+每种规格的采购量）。
**举例：**
		VID 3 @ $570 5 @ $900 9 @ $1530 -- 产品类型 & 包裹规格 和 价格
		VID 13 -- 商品类型 & 需要的订单额
希望得到的结果：
		13 VID $2370
		2 x 5 $1800
		1 x 3 $570

#### 目录结构

```
smartBundleUnpack
├── gradle （1）
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew （2）
├── gradlew.bat （2）
├── settings.gradle （3）
└── app
    ├── build.gradle （4）
    ├── cfg          （5）
    │   └── setBundles.cfg
    ├── log          （6）
    └── src
        ├── main
        │   └── java （7）
        │       └── bundleUnpack
        │                 ├── App.java          （7-1）
        │                 ├── Bundle.java       （7-2）
        │                 ├── BundleS.java      （7-3）
        │                 ├── DivideBundle.java （7-4）
        │                 ├── MyFormat.java     （7-5）
        │                 └── NumPrice.java     （7-6）
        └── test
            └── java （8）
                └── bundleUnpack
                    └── AppTest.java
```

（1）~（3）不用修改，执行 gradle init 命令创建项目时自动生成的；

（4）build.gradle文件中增加了 对 lombok  的依赖；

```
dependencies {
	...
  //Lisa 使用 lombok
  compileOnly 'org.projectlombok:lombok:1.18.22'
	annotationProcessor 'org.projectlombok:lombok:1.18.22'
	
	testCompileOnly 'org.projectlombok:lombok:1.18.22'
	testAnnotationProcessor 'org.projectlombok:lombok:1.18.22'
}
```

（5）cfg / setBundles.cfg 中保存了 商品配置信息，程序运行后会读取该文件；

（6）log 文件夹保存程序运行的中间结果 及 最终输出的方案；

文件 logBundleFLAT.log 输出了类型为FLAT的商品的处理过程，其他两个类似；

文件 logOrder.log 显示了配置文件中读出的信息、三个订单需求及各自输出方案，如下图：

```
INFO: 2022-02-19 10:10:49 164 --- initBundles 从文件 setBundles.log 读取：产品类型 及 Bundle 规格价格
INFO: 2022-02-19 10:10:49 347 	类型：IMG
INFO: 2022-02-19 10:10:49 349 	5 @ 450.0
INFO: 2022-02-19 10:10:49 350 	10 @ 800.0
INFO: 2022-02-19 10:10:49 391 	类型：FLAC
INFO: 2022-02-19 10:10:49 393 	3 @ 427.5
INFO: 2022-02-19 10:10:49 394 	6 @ 810.0
INFO: 2022-02-19 10:10:49 395 	9 @ 1147.5
INFO: 2022-02-19 10:10:49 446 	类型：VID
INFO: 2022-02-19 10:10:49 447 	3 @ 570.0
INFO: 2022-02-19 10:10:49 448 	5 @ 900.0
INFO: 2022-02-19 10:10:49 450 	9 @ 1530.0
INFO: 2022-02-19 10:10:49 459 ---- 下单：类型 VID 数量 13 ----
INFO: 2022-02-19 10:10:49 481 ---- 花费：2370.0 分解如下：
INFO: 2022-02-19 10:10:49 485 		1 * 3 ~ 570.0
INFO: 2022-02-19 10:10:49 489 		2 * 5 ~ 900.0
INFO: 2022-02-19 10:10:49 490 ---- 下单：类型 IMG 数量 10 ----
INFO: 2022-02-19 10:10:49 502 ---- 花费：800.0 分解如下：
INFO: 2022-02-19 10:10:49 505 		1 * 10 ~ 800.0
INFO: 2022-02-19 10:10:49 506 ---- 下单：类型 FLAC 数量 15 ----
INFO: 2022-02-19 10:10:49 513 ---- 花费：1957.5 分解如下：
INFO: 2022-02-19 10:10:49 514 		1 * 6 ~ 810.0
INFO: 2022-02-19 10:10:49 517 		1 * 9 ~ 1147.5
```

（7）代码实现

主要文件说明如图：

```
 │       └── bundleUnpack
                 ├── App.java     （7-1） 定义 main 函数，程序运行入口        
                 ├── BundleS.java （7-3） 读取配置文件，初始化，提供下单接口
                 ├── Bundle.java  （7-2） 对某种商品的处理
                 ├── MyFormat.java（7-5） 设置日志打印形式
                 ├── 其他基础数据类定义
```

（8）自动生成的测试代码

#### 怎么运行

下载到本地后，smartBundleUnpack文件夹内容如上图所示。

使用VSCode打开smartBundleUnpack文件夹，在VSCode命令行输入：

```
$gradlew run

...
BUILD SUCCESSFUL in 1s
2 actionable tasks: 2 executed
```

即可编译并成功运行，检查 smartBundleUnpack/app/log，可见生成的运行记录文件（接上图）如下：

```
├── log          （6）
    └── src
        └── logBundleFLAC.log
        ├── logBundleIMG.log
        ├── logBundleVID.log
        └── logOrder.log
```

