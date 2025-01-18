# Matplotlib
- Importing

```py
import matplotlib.pyplot as plt
```

- Common params
  1. label : Line label
  2. color : Color of line
  3. linestyle : dotted, dashed, solid, dashdot ...
  4. Marker : Shape at point (x,y)  

- Common plot methods
  1. plt.title : Title of plot
  2. plt.xlabel
  3. plt.ylabel
  4. plt.legend

- Subplots
  - Used to divide single figure into multiple spaces
  - plt.subplot(number_of_rows, number_of_columns, current_position)
  - if there is 2x2 matrix of plots then numbering is as
  - 
    ```py
    [
      [1,2]
      [3,4]
    ]
    ```

  - Use `plt.tight_layout()` to arrange spaces between plots
 
- Basic types of plot
  1. plot
       - `plt.plot(x,y)`
  2. Bar chart
       - `plt.bar(categories, values)`
       - categores goes on x and are under rectangle
       - Values goes on vertical
  3. Horizontal Bar char
       - `plt.barh(categories, value)`
       - categories goes under rectangle
  4. Scatter plot
       - `plt.scatter(x,y,s=sizes,c=color, cmap, alpha, edgecolor)`
       - sizes(array) = size of each point
       - color(array) = color of each point
       - cmap = color mapping (int to color mapping)
       - alpha = 0 - 1 range | transparency of points
       - edgecolor
  5. Histogram
       - It shows distribution of data
       - `plt.hist(data, bins, edgecolor, alpha)`
       - data = 1D array
       - bins = number of rectangles
       - edgecolor
       - alpha 
  6. PieChart
       - `plt.pie(sizes, labels,explode,autopct, startangle, colors)`
       - sizes(array) = Size of each entry
       - labels(array) = labels on each entry
       - explode(array) = how far from center
       - autopct = how to display number "%1.4f%%" : 4 decimals after zero
     
