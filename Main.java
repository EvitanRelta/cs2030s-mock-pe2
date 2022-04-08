import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class Main {
  
  public static Stream<Point> pointStream(Point point, Function<Point, Point> f) {
    return Stream.iterate(point, p -> f.apply(p));
  }

  public static Stream<Point> generateGrid(Point point, int n) {
    return pointStream(
        point,
        p -> new Point(p.getX() + 1, p.getY())
    )
        .flatMap(startingPt -> pointStream(
            startingPt,
            p -> new Point(p.getX(), p.getY() + 1)
        ).limit(n))
        .limit(n * n);
  }

  public static Stream<Circle> concentricCircles(Circle circle, Function<Double, Double> f) {
    return Stream.iterate(
        circle,
        c -> new Circle(
            c.getCenter(),
            f.apply(c.getRadius())
        )
    );
  }

  public static Stream<Point> pointStreamFromCircle(Stream<Circle> circles) {
    return circles.map(c -> c.getCenter());
  }

  public static double estimatePi(Circle c, Supplier<RandomPoint> supplier, int k) {
    return Stream.generate(supplier)
        .limit(k)
        .filter(p -> c.contains(p))
        .reduce(0.0, (acc, p) -> acc + 1, Double::sum) * 4.0 / k;
        //.count() * 4.0 / k;
  }
}
