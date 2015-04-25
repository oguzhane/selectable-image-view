selectable-image-view
=================

Selectable image view in Android. Simple, easy, high draw performance


----------




![Imgur](http://i.imgur.com/OyeJuMV.gif)

Usage
--------

###XML
```xml
<com.oguzhane.view.SelectableImageView
    app:siv_radiusDy="128dp"
    app:siv_radiusDx="128dp"

    app:siv_border="true"
    app:siv_borderColor="#ff000000"
    app:siv_borderWidth="3dp"

    app:siv_selectStrokeWidth="3dp"
    app:siv_selectStrokeColor="#ff000000"
    app:siv_selectColor="#64000000"

    app:siv_selectImageSrc="@drawable/checkmark_64"
    app:siv_selectImageWidth="64dp"
    app:siv_selectImageHeight="64dp"
    app:siv_selectImageFilter="#fff40025"
    app:siv_selectImageTransparency="255"
    />
```



###JAVA

```java
       SelectableImageView siv = (SelectableImageView)findViewById(R.id.siv_gender);
        siv.setSelectionListener(new SelectableImageView.SelectionListener() {
            @Override
            public void OnSelected(View v) {
                //onselected
            }

            @Override
            public void OnUnSelected(View v) {
	            //onunselected
            }
        });
```

### Maven
*Coming soon Maven module*

License
--------

    The MIT License (MIT)
    
    Copyright (c) 2015 Oguzhan ERGIN
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

