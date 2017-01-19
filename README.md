# SlideView
SlideView是一个Android自定义滑动确认控件，当然使用的场景不仅限于滑动确认。

##Snapshots
<img src="https://github.com/Gnepux/SlideView/blob/master/snapshot/slideview1.jpeg" height = "600" alt="slideview1" align=center />
<img src="https://github.com/Gnepux/SlideView/blob/master/snapshot/slideview2.jpeg" height = "600" alt="slideview2" align=center />
<img src="https://github.com/Gnepux/SlideView/blob/master/snapshot/slideview3.jpeg" height = "600" alt="slideview2" align=center />


##使用方式
在layout引用com.gnepux.slideview.SlideView
```xml
<com.gnepux.slideview.SlideView
    android:id="@+id/slideview"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:bg_text="滑动开始确认"    
    app:bg_text_color="@color/white"
    app:bg_text_size="18sp"
    app:bg_text_complete="松开即可确认"
    app:icon_drawable="@drawable/btn_confirm_wy_yellow"
    app:min_height="48dp"
    app:icon_ratio="0.15"
    app:enable_when_full="false"
    app:reset_not_full="true"
    app:secondary_color="@color/yellow"
    app:bg_drawable="@drawable/bg_slide_view"
    app:gradient_color1="#0a0a0a"
    app:gradient_color2="#FFFFFF"
    app:gradient_color3="#0a0a0a"/>
```
目前支持的自定义属性有：
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="SlideView">
        <!--背景图片-->
        <attr name="bg_drawable" format="reference"/>
        <!--按钮的背景图-->
        <attr name="icon_drawable" format="reference"/>
        <!--按钮上显示的文字-->
        <attr name="icon_text" format="string"/>
        <!--按钮上文字的颜色-->
        <attr name="icon_text_color" format="color"/>
        <!--按钮上文字的大小-->
        <attr name="icon_text_size" format="dimension"/>
        <!--按钮宽占总宽度的比例-->
        <attr name="icon_ratio" format="float"/>
        <!--背景文字-->
        <attr name="bg_text" format="string"/>
        <!--拖动完成的背景文字-->
        <attr name="bg_text_complete" format="string"/>
        <!--背景文字的颜色-->
        <attr name="bg_text_color" format="color"/>
        <!--背景文字的大小-->
        <attr name="bg_text_size" format="dimension"/>
        <!--控件最小高度-->
        <attr name="min_height" format="dimension"/>
        <!--已拖动部分的颜色-->
        <attr name="secondary_color" format="color"/>
        <!--拖动到一半松开是否重置按钮-->
        <attr name="reset_not_full" format="boolean"/>
        <!--拖动结束后是否可以再次操作-->
        <attr name="enable_when_full" format="boolean"/>
        <!-- 背景文字渐变颜色1 -->
        <attr name="gradient_color1" format="color"/>
        <!-- 背景文字渐变颜色2 -->
        <attr name="gradient_color2" format="color"/>
        <!-- 背景文字渐变颜色3 -->
        <attr name="gradient_color3" format="color"/>
    </declare-styleable>
</resources>
```
目前支持的API有：
```java
/** 
 * 重置SlideView
 */
void reset();

/** 
 * 设置'滑动完成松开后是否可继续拖动'
 */
void enableWhenFull(boolean enable);

/** 
 * 获取'滑动完成松开后是否可继续拖动'属性值
 */
boolean isEnableWhenFull();

/** 
 * 设置'滑动未完成松开是否复原'
 */
void resetWhenNotFull(boolean reset);

/** 
 * 获取'滑动未完成松开是否复原'属性值
 */
boolean isResetWhenNotFull();

/** 
 * 设置滑动完成松开后的监听
 */
void addSlideListener(OnSlideListener listener);
```
##实现原理
[Android自定义滑动确认控件SlideView](https://my.oschina.net/u/3026396/blog/817257)

##联系作者
QQ: 386832294<br/>
wehat: Mrfeijie(386832294)<br/>
email: roc_hsu@163.com
