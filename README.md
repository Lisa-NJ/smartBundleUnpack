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
logBundleProcess.log
BreakdownPlan.log
```

breakdownPlan.log is the answer to the given order.

#### How is it worked out

If an order number is received, let's say it is targetNumber, and the total number is what we are to get when the order number is divided into different sizes, besides, the break down plan is also what we need to work out.

Let's take bundle format 'VID 3 @ $570 5 @ $900 9 @ $1530'  and an order number 8 as an example. 

In this example, the targetNumber is 8, the bundle sizes are 3, 5, 9, then the break down plan is like: 1, 1, 0 because 1 * 3 + 1 * 5 + 0 * 9 = 8， and at the same time the total number is 8, which is exactly the targetNumber.

However, there are cases where the total number is bigger than the targetNumber.

For example, if an order number is 7, then total number should be 8 and the break down plan would be the same as the above example. 

Let's go to the algorithm. Suppose the bundle format is 'VID 3 @ $570 5 @ $900 9 @ $1530' and the maximum bundle size is MaxS. 

First, initialise the base data and work out the total number and breakdown plan for each target number from 1 to 9 -- MaxS following the rules: 

1. If target number == bundle size i, the total number =  target number, the bundle number is 1, and the bundle break down plan looks like 0 1 0 and the postion of 1 in the array is i;

2. If target number < bundle size 0, the total number =  bundle size 0, the bundle number is 1, and the bundle break down plan is 1 0 0;

3. If a target number is less than MaxS, and it is not any bundle size given, the total number is the minimum of these two numbers: 

   The first number is the next budle size immediately after it; If target number is 7, the first number = 9;

   The second number is the total number to be calculated with the bundle sizes without the current MaxS. If target number is 7, the second number = the total number when given bundle sizes are 3, 5~~, 9~~;

   After the initialisation of the base data, the base data looks like,

   ```
   targetNumber          total number                     breakdown plan
   	1                         3                            1,0,0
   	2                         3                            1,0,0
   	3                         3                            1,0,0
   	4                         5                            0,1,0
   	5                         5                            0,1,0
   	6                         6                            2,0,0
   	7                         9                            0,0,1
   	8                         9                            0,0,1
   	9 MaxS                    9                            0,0,1
   ```

Second, if an order number is received and it is not bigger than MaxS, return the result from the base data; however, if an order number is bigger than MaxS, the total number falls into these two number, suppose MaxS2 is the seccond biggest number of bundle sizes:

The first number = MaxS + total number with new order number being order number less by MaxS;

The second number = MaxS2 + total number with new order number being  order number less by MaxS2 and new budle sizes being old bundle sizes without MaxS;

Then return the minimun of them.

#### Dependencies

To ensure the project to run correctly, these third party tools are needed:

|      | Name   | Version |
| ---- | ------ | ------- |
| 1    | Gradle | 7.4     |
| 2    | Lombok | 1.18.22 |
| 3    | Junit  | 4.13.2  |

Enjoy!

