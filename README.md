#### What is this for?

Given a brands order, determine the cost and bundle breakdown for each submission format. 

Bundles： 

​				IMG 5 @ $450 10 @ $800 

​				FLAC 3 @ $427.50 6 @ $810 9 @ $1147.50 

​				VID 3 @ $570 5 @ $900 9 @ $1530



Input： 	IMG 10 15 FLAC 15 VID 13

Output：10 IMG $800 -- 1 x 10 $800 

​                15 FLAC $1957.50 -- 1 x 9 $1147.50 + 1 x 6 $810 

​				13 VID $2370 -- 2 x 5 $1800 + 1 x 3 $570

#### How to use

Download the project to your computer，and you can see the folder smartBundleUnpack.

The file cfg/Order.cfg is used for the order information, and it can be changed as needed;

The file cfg/BundleFormat is used for the bundle format, and it is changeable too.

Open smartBundleUnpack in IDE(VScode or Intellij IDEA)，and  input the sentence as follows in the terminal window：

```
$gradlew run

...
BUILD SUCCESSFUL in 1s
2 actionable tasks: 2 executed
```

Check smartBundleUnpack/app/log，and you can see the following files. ：

```
├── log          （6）
		└── logBundleProcess.log
		└── BreakdownPlan.log
```

BreakdownPlan.log is the answer to the given order.

#### How is it worked out

Let's take bunle fomat 'VID 3 @ $570 5 @ $900 9 @ $1530' as an example. 

If an order number is received, let's say it is targetNumber.



#### Dependencies

To ensure the project to run correctly, these third party tools are needed:

|      | Name   | Version |
| ---- | ------ | ------- |
| 1    | Gradle | 7.4     |
| 2    | Lombok | 1.18.22 |
| 3    | Junit  | 4.13.2  |

Enjoy!

