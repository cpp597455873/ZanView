/*
 * Copyright (C) 2015, 程序亦非猿
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simonw.view.zan;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * 三次贝塞尔曲线
 *
 * @author chenpiaopiao
 * @date 2017/11/29 16:00
 */

public class LikeBezierEvaluator implements TypeEvaluator<Point> {


    private Point controlPoint1;
    private Point controlPoint2;
    private Point point = new Point();//结果


    public LikeBezierEvaluator(Point controlPoint1, Point controlPoint2) {
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
    }

    @Override
    public Point evaluate(float time, Point startValue, Point endValue) {
        float timeLeft = 1.0f - time;
        // 贝塞尔曲线公式
        point.x = (int) (timeLeft * timeLeft * timeLeft * (startValue.x) + 3 * timeLeft * timeLeft * time * (controlPoint1.x) + 3 * timeLeft * time * time * (controlPoint2.x) + time * time * time * (endValue.x));
        point.y = (int) (timeLeft * timeLeft * timeLeft * (startValue.y) + 3 * timeLeft * timeLeft * time * (controlPoint1.y) + 3 * timeLeft * time * time * (controlPoint2.y) + time * time * time * (endValue.y));
        return point;
    }
}
