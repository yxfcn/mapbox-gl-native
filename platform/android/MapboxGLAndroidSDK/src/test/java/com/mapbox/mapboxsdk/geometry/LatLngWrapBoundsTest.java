package com.mapbox.mapboxsdk.geometry;

import android.os.Parcelable;
import com.mapbox.mapboxsdk.constants.GeometryConstants;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.utils.MockParcel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LatLngWrapBoundsTest {

  private static final double DELTA = 1e-15;

  private LatLngWrapBounds latLngWrapBounds;
  private static final LatLng LAT_LNG_NULL_ISLAND = new LatLng(0, 0);
  private static final LatLng LAT_LNG_NOT_NULL_ISLAND = new LatLng(2, 2);

  @Before
  public void beforeTest() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NULL_ISLAND)
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .build();
  }

  @Test
  public void testSanity() {
    LatLngWrapBounds.Builder LatLngWrapBoundsBuilder = new LatLngWrapBounds.Builder();
    LatLngWrapBoundsBuilder.include(LAT_LNG_NULL_ISLAND).include(LAT_LNG_NOT_NULL_ISLAND);
    assertNotNull("latLng  should not be null", LatLngWrapBoundsBuilder.build());
  }

  @Test(expected = InvalidLatLngBoundsException.class)
  public void noLatLngs() {
    new LatLngWrapBounds.Builder().build();
  }

  @Test(expected = InvalidLatLngBoundsException.class)
  public void oneLatLngs() {
    new LatLngWrapBounds.Builder().include(LAT_LNG_NULL_ISLAND).build();
  }

  @Test
  public void latitiudeSpan() {
    assertEquals("Span should be the same", 2, latLngWrapBounds.getLatitudeSpan(), DELTA);
  }

  @Test
  public void longitudeSpan() {
    assertEquals("Span should be the same", 2, latLngWrapBounds.getLongitudeSpan(), DELTA);
  }

  @Test
  public void coordinateSpan() {
    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be the same", new LatLngSpan(2, 2), latLngSpan);
  }

  @Test
  public void dateLineSpanBuilder1() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, -170))
      .include(new LatLng(-10, 170))
      .build();

    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be shortest distance", new LatLngSpan(20, 20),
      latLngSpan);
  }

  @Test
  public void dateLineSpanBuilder2() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(-10, -170))
      .include(new LatLng(10, 170))
      .build();

    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be shortest distance", new LatLngSpan(20, 20),
      latLngSpan);
  }

  @Test
  public void dateLineSpanFrom1() {
    latLngWrapBounds = LatLngWrapBounds.from(10, -170, -10, 170);
    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be shortest distance", new LatLngSpan(20, 20),
      latLngSpan);
  }

  @Test
  public void dateLineSpanFrom2() {
    latLngWrapBounds = LatLngWrapBounds.from(10, 170, -10, -170);
    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be shortest distance", new LatLngSpan(20, 340),
      latLngSpan);
  }

  @Test
  public void zeroLongitudeSpan() {
    latLngWrapBounds = LatLngWrapBounds.from(10, 10, -10, 10);
    LatLngSpan latLngSpan = latLngWrapBounds.getSpan();
    assertEquals("LatLngSpan should be shortest distance", new LatLngSpan(20, 0),
      latLngSpan);
  }

  @Test
  public void nearDateLineCenter1() {
    latLngWrapBounds = LatLngWrapBounds.from(10, -175, -10, 165);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, 175), center);
  }

  @Test
  public void nearDateLineCenter2() {
    latLngWrapBounds = LatLngWrapBounds.from(10, -165, -10, 175);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, -175), center);
  }

  @Test
  public void nearDateLineCenter3() {
    latLngWrapBounds = LatLngWrapBounds.from(10, -170, -10, 170);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, -180), center);
  }

  @Test
  public void nearDateLineCenter4() {
    latLngWrapBounds = LatLngWrapBounds.from(10, -180, -10, 0);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, 90), center);
  }

  @Test
  public void nearDateLineCenter5() {
    latLngWrapBounds = LatLngWrapBounds.from(10, 180, -10, 0);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, 90), center);
  }

  @Test
  public void centerForBoundsWithSameLongitude() {
    latLngWrapBounds = LatLngWrapBounds.from(10, 10, -10, 10);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(0, 10), center);
  }

  @Test
  public void centerForBoundsWithSameLatitude() {
    latLngWrapBounds = LatLngWrapBounds.from(10, 10, 10, -10);
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(10, 0), center);
  }

  @Test
  public void center() {
    LatLng center = latLngWrapBounds.getCenter();
    assertEquals("Center should match", new LatLng(1, 1), center);
  }

  @Test
  public void notEmptySpan() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .include(LAT_LNG_NULL_ISLAND)
      .build();
    assertFalse("Should not be empty", latLngWrapBounds.isEmptySpan());
  }

  @Test
  public void includeSameLatLngs() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .include(LAT_LNG_NULL_ISLAND)
      .include(LAT_LNG_NULL_ISLAND)
      .build();
    assertEquals(latLngWrapBounds.getNorthEast(), LAT_LNG_NOT_NULL_ISLAND);
    assertEquals(latLngWrapBounds.getSouthWest(), LAT_LNG_NULL_ISLAND);
  }

  @Test
  public void toLatLngs() {
    latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .include(LAT_LNG_NULL_ISLAND)
      .build();

    assertArrayEquals("LatLngs should match",
      new LatLng[] {LAT_LNG_NOT_NULL_ISLAND, LAT_LNG_NULL_ISLAND},
      latLngWrapBounds.toLatLngs());
  }

  @Test
  public void include() {
    assertTrue("LatLng should be included", latLngWrapBounds.contains(new LatLng(1, 1)));
  }

  @Test
  public void includes() {
    List<LatLng> points = new ArrayList<>();
    points.add(LAT_LNG_NULL_ISLAND);
    points.add(LAT_LNG_NOT_NULL_ISLAND);

    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .includes(points)
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NULL_ISLAND)
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .build();

    assertEquals("LatLngWrapBounds should match", LatLngWrapBounds1, LatLngWrapBounds2);
  }

  @Test
  public void includesOrderDoesNotMatter() {
    LatLngWrapBounds sameLongitudeFirst = new LatLngWrapBounds.Builder()
      .include(new LatLng(50, 10))  // southWest
      .include(new LatLng(60, 10))
      .include(new LatLng(60, 20))  // northEast
      .include(new LatLng(50, 20))
      .include(new LatLng(50, 10))  // southWest again
      .build();

    LatLngWrapBounds sameLatitudeFirst = new LatLngWrapBounds.Builder()
      .include(new LatLng(50, 20))
      .include(new LatLng(50, 10))  // southWest
      .include(new LatLng(60, 10))
      .include(new LatLng(60, 20))  // northEast
      .include(new LatLng(50, 20))
      .build();

    assertEquals(sameLatitudeFirst, sameLongitudeFirst);
  }

  @Test
  public void includesOverDateline1() {

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, -170))
      .include(new LatLng(-10, -175))
      .include(new LatLng(0, 170))
      .build();

    assertEquals("LatLngSpan should be the same",
      new LatLngSpan(20, 20), latLngWrapBounds.getSpan());
  }

  @Test
  public void includesOverDateline2() {

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 170))
      .include(new LatLng(-10, 175))
      .include(new LatLng(0, -170))
      .build();

    assertEquals("LatLngSpan should be the same",
      new LatLngSpan(20, 20), latLngWrapBounds.getSpan());
  }

  @Test
  public void includesOverDateline3() {

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 170))
      .include(new LatLng(-10, -170))
      .include(new LatLng(0, -180))
      .include(new LatLng(5, 180))
      .build();

    assertEquals("LatLngSpan should be the same",
      new LatLngSpan(20, 20), latLngWrapBounds.getSpan());
  }

  @Test
  public void containsNot() {
    assertFalse("LatLng should not be included", latLngWrapBounds.contains(new LatLng(3, 1)));
  }

  @Test
  public void containsBoundsInWorld() {
    assertTrue("LatLngWrapBounds should be contained in the world",
            LatLngWrapBounds.world().contains(latLngWrapBounds));
  }

  @Test
  public void worldSpan() {
    assertEquals("LatLngWrapBounds world span should be 180, 360",
      GeometryConstants.LATITUDE_SPAN, LatLngWrapBounds.world().getLatitudeSpan(), DELTA);
    assertEquals("LatLngWrapBounds world span should be 180, 360",
      GeometryConstants.LONGITUDE_SPAN, LatLngWrapBounds.world().getLongitudeSpan(), DELTA);
  }

  @Test
  public void emptySpan() {
    LatLngWrapBounds latLngWrapBounds = LatLngWrapBounds.from(
            GeometryConstants.MIN_LATITUDE, GeometryConstants.MAX_WRAP_LONGITUDE,
            GeometryConstants.MIN_LATITUDE, GeometryConstants.MAX_WRAP_LONGITUDE);
    assertTrue("LatLngWrapBounds empty span", latLngWrapBounds.isEmptySpan());
  }

  @Test
  public void containsBounds() {
    LatLngWrapBounds inner = new LatLngWrapBounds.Builder()
      .include(new LatLng(-5, -5))
      .include(new LatLng(5, 5))
      .build();
    LatLngWrapBounds outer = new LatLngWrapBounds.Builder()
      .include(new LatLng(-10, -10))
      .include(new LatLng(10, 10))
      .build();
    assertTrue(outer.contains(inner));
    assertFalse(inner.contains(outer));
  }

  @Test
  public void testHashCode() {
    assertEquals(2147483647, latLngWrapBounds.hashCode(), -1946419200);
  }

  @Test
  public void equality() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(LAT_LNG_NULL_ISLAND)
      .include(LAT_LNG_NOT_NULL_ISLAND)
      .build();
    assertEquals("equality should match", this.latLngWrapBounds, latLngWrapBounds);
    assertEquals("not equal to a different object type", this.latLngWrapBounds.equals(LAT_LNG_NOT_NULL_ISLAND), false);
  }

  @Test
  public void testToString() {
    assertEquals(latLngWrapBounds.toString(), "N:2.0; E:2.0; S:0.0; W:0.0");
  }

  @Test
  public void intersect() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(1, 1))
      .include(LAT_LNG_NULL_ISLAND)
      .build();
    assertEquals("intersect should match", latLngWrapBounds,
            latLngWrapBounds.intersect(this.latLngWrapBounds.getLatNorth(),
                    this.latLngWrapBounds.getLonEast(),
                    this.latLngWrapBounds.getLatSouth(),
                    this.latLngWrapBounds.getLonWest()));
  }

  @Test
  public void intersectNot() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 10))
      .include(new LatLng(9, 8))
      .build();
    assertNull(latLngWrapBounds.intersect(this.latLngWrapBounds));
  }

  @Test
  public void intersectNorthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds intersectlatLngWrapBounds =
      LatLngWrapBounds.from(10, 10, 0, 0)
        .intersect(200, 200, 0, 0);
  }

  @Test
  public void intersectSouthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds intersectlatLngWrapBounds =
      LatLngWrapBounds.from(0, 0, -10, -10)
        .intersect(0, 0, -200, -200);
  }

  @Test
  public void intersectSouthLessThanNorthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latNorth cannot be less than latSouth");

    LatLngWrapBounds intersectlatLngWrapBounds =
      LatLngWrapBounds.from(10, 10, 0, 0)
        .intersect(0, 200, 20, 0);
  }


  @Test
  public void intersectEastWrapCheck() {

    LatLngWrapBounds LatLngWrapBounds1 = LatLngWrapBounds.from(10, -150, 0, 0);
    LatLngWrapBounds LatLngWrapBounds2 = LatLngWrapBounds.from(90, 200, 0, 0);

    LatLngWrapBounds intersectLatLngWrapBounds = LatLngWrapBounds.from(10, -160, 0, 0);

    assertEquals(LatLngWrapBounds1.intersect(LatLngWrapBounds2), intersectLatLngWrapBounds);
    assertEquals(LatLngWrapBounds2.intersect(LatLngWrapBounds1), intersectLatLngWrapBounds);
  }

  @Test
  public void intersectWestWrapCheck() {
    LatLngWrapBounds LatLngWrapBounds1 = LatLngWrapBounds.from(0, 0, -10, 150);
    LatLngWrapBounds LatLngWrapBounds2 = LatLngWrapBounds.from(0, 0, -90, -200);

    LatLngWrapBounds intersectLatLngWrapBounds = LatLngWrapBounds.from(0, 0, -10, 160);

    assertEquals(LatLngWrapBounds1.intersect(LatLngWrapBounds2), intersectLatLngWrapBounds);
    assertEquals(LatLngWrapBounds2.intersect(LatLngWrapBounds1), intersectLatLngWrapBounds);
  }

  @Test
  public void innerUnion() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(1, 1))
      .include(LAT_LNG_NULL_ISLAND)
      .build();
    assertEquals("union should match", latLngWrapBounds, latLngWrapBounds.intersect(this.latLngWrapBounds));
  }

  @Test
  public void outerUnion() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 10))
      .include(new LatLng(9, 8))
      .build();
    assertEquals("outer union should match",
      latLngWrapBounds.union(this.latLngWrapBounds),
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 10))
        .include(LAT_LNG_NULL_ISLAND)
        .build());
  }

  @Test
  public void unionOverDateLine() {
    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 170))
      .include(new LatLng(0, 160))
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(new LatLng(0, -170))
      .include(new LatLng(-10, -160))
      .build();

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1,
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 160))
        .include(new LatLng(-10, -160))
        .build());

    assertEquals(union1, union2);
  }

  @Test
  public void unionOverDateLine2() {
    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 170))
      .include(new LatLng(0, 160))
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(new LatLng(0, 165))
      .include(new LatLng(-10, -160))
      .build();

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1,
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 160))
        .include(new LatLng(-10, -160))
        .build());

    assertEquals(union1, union2);
  }

  @Test
  public void unionOverDateLine3() {
    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, -165))
      .include(new LatLng(0, 160))
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(new LatLng(0, -170))
      .include(new LatLng(-10, -160))
      .build();

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1,
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 160))
        .include(new LatLng(-10, -160))
        .build());

    assertEquals(union1, union2);
  }

  @Test
  public void unionOverDateLine4() {
    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, -160))
      .include(new LatLng(0, 160))
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(new LatLng(0, -170))
      .include(new LatLng(-10, -175))
      .build();

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1,
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 160))
        .include(new LatLng(-10, -160))
        .build());

    assertEquals(union1, union2);
  }

  @Test
  public void unionOverDateLine5() {
    LatLngWrapBounds LatLngWrapBounds1 = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, -160))
      .include(new LatLng(0, 160))
      .build();

    LatLngWrapBounds LatLngWrapBounds2 = new LatLngWrapBounds.Builder()
      .include(new LatLng(0, 170))
      .include(new LatLng(-10, 175))
      .build();

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1,
      new LatLngWrapBounds.Builder()
        .include(new LatLng(10, 160))
        .include(new LatLng(-10, -160))
        .build());

    assertEquals(union1, union2);
  }

  @Test
  public void unionOverDateLineReturnWorldLonSpan() {
    LatLngWrapBounds LatLngWrapBounds1 = LatLngWrapBounds.from(10, -160, -10, -10);
    LatLngWrapBounds LatLngWrapBounds2 = LatLngWrapBounds.from(10, 10, -10, 160);

    LatLngWrapBounds union1 = LatLngWrapBounds1.union(LatLngWrapBounds2);
    LatLngWrapBounds union2 = LatLngWrapBounds2.union(LatLngWrapBounds1);

    assertEquals(union1, union2);
    assertEquals(union1, LatLngWrapBounds.from(10, 180, -10, -180));
  }

  @Test
  public void unionNorthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds unionlatLngWrapBounds =
      LatLngWrapBounds.from(10, 10, 0, 0)
        .union(200, 200, 0, 0);
  }

  @Test
  public void unionSouthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds unionlatLngWrapBounds =
      LatLngWrapBounds.from(0, 0, -10, -10)
        .union(0, 0, -200, -200);
  }

  @Test
  public void unionSouthLessThanNorthCheck() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latNorth cannot be less than latSouth");

    LatLngWrapBounds unionlatLngWrapBounds =
      LatLngWrapBounds.from(10, 10, 0, 0)
        .union(0, 200, 20, 0);
  }


  @Test
  public void unionEastWrapCheck() {

    LatLngWrapBounds LatLngWrapBounds1 = LatLngWrapBounds.from(10, 10, 0, 0);
    LatLngWrapBounds LatLngWrapBounds2 = LatLngWrapBounds.from(90, 200, 0, 0);
    LatLngWrapBounds unionLatLngWrapBounds = LatLngWrapBounds.from(90, -160, 0, 0);

    assertEquals(LatLngWrapBounds1.union(LatLngWrapBounds2), unionLatLngWrapBounds);
    assertEquals(LatLngWrapBounds2.union(LatLngWrapBounds1), unionLatLngWrapBounds);
  }

  @Test
  public void unionWestWrapCheck() {
    LatLngWrapBounds LatLngWrapBounds1 = LatLngWrapBounds.from(0, 0, -10, -10);
    LatLngWrapBounds LatLngWrapBounds2 = LatLngWrapBounds.from(0, 0, -90, -200);

    LatLngWrapBounds unionLatLngWrapBounds = LatLngWrapBounds.from(0, 0, -90, 160);

    assertEquals(LatLngWrapBounds1.union(LatLngWrapBounds2), unionLatLngWrapBounds);
    assertEquals(LatLngWrapBounds2.union(LatLngWrapBounds1), unionLatLngWrapBounds);
  }

  @Test
  public void northWest() {
    double minLat = 5;
    double minLon = 6;
    double maxLat = 20;
    double maxLon = 21;

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(minLat, minLon))
      .include(new LatLng(maxLat, maxLon))
      .build();

    assertEquals("NorthWest should match", latLngWrapBounds.getNorthWest(), new LatLng(maxLat, minLon));
  }

  @Test
  public void southWest() {
    double minLat = 5;
    double minLon = 6;
    double maxLat = 20;
    double maxLon = 21;

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(minLat, minLon))
      .include(new LatLng(maxLat, maxLon))
      .build();

    assertEquals("SouthWest should match", latLngWrapBounds.getSouthWest(), new LatLng(minLat, minLon));
  }

  @Test
  public void northEast() {
    double minLat = 5;
    double minLon = 6;
    double maxLat = 20;
    double maxLon = 21;

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(minLat, minLon))
      .include(new LatLng(maxLat, maxLon))
      .build();

    assertEquals("NorthEast should match", latLngWrapBounds.getNorthEast(), new LatLng(maxLat, maxLon));
  }

  @Test
  public void southEast() {
    double minLat = 5;
    double minLon = 6;
    double maxLat = 20;
    double maxLon = 21;

    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(minLat, minLon))
      .include(new LatLng(maxLat, maxLon))
      .build();

    assertEquals("SouthEast should match", latLngWrapBounds.getSouthEast(), new LatLng(minLat, maxLon));
  }

  @Test
  public void testParcelable() {
    LatLngWrapBounds latLngWrapBounds = new LatLngWrapBounds.Builder()
      .include(new LatLng(10, 10))
      .include(new LatLng(9, 8))
      .build();
    Parcelable parcel = MockParcel.obtain(latLngWrapBounds);
    assertEquals("Parcel should match original object", parcel, latLngWrapBounds);
  }

  @Test
  public void fromTileID() {
    LatLngWrapBounds bounds = LatLngWrapBounds.from(0, 0, 0);
    assertEquals(GeometryConstants.MIN_WRAP_LONGITUDE, bounds.getLonWest(), DELTA);
    assertEquals(GeometryConstants.MIN_MERCATOR_LATITUDE, bounds.getLatSouth(), DELTA);
    assertEquals(GeometryConstants.MAX_WRAP_LONGITUDE, bounds.getLonEast(), DELTA);
    assertEquals(GeometryConstants.MAX_MERCATOR_LATITUDE, bounds.getLatNorth(), DELTA);

    bounds = LatLngWrapBounds.from(10, 288, 385);
    assertEquals(-78.75, bounds.getLonWest(), DELTA);
    assertEquals(40.446947059600497, bounds.getLatSouth(), DELTA);
    assertEquals(-78.3984375, bounds.getLonEast(), DELTA);
    assertEquals(40.713955826286039, bounds.getLatNorth(), DELTA);

  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testConstructorChecksNorthLatitudeNaN() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must not be NaN");
    LatLngWrapBounds.from(Double.NaN, 0, -20, -20);
  }

  @Test
  public void testConstructorChecksEastLongitudeNaN() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("longitude must not be NaN");
    LatLngWrapBounds.from(0, Double.NaN, -20, -20);
  }

  @Test
  public void testConstructorChecksNorthLatitudeGreaterThan90() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds.from(95, 0, -20, -20);
  }

  @Test
  public void testConstructorChecksNorthLatitudeLessThanThanNegative90() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds.from(-95, 0, -20, -20);
  }

  @Test
  public void testConstructorChecksEastLongitudeInfinity() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("longitude must not be infinite");
    LatLngWrapBounds.from(0, Double.POSITIVE_INFINITY, -20, -20);
  }

  @Test
  public void testConstructorChecksSouthLatitudeNaN() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must not be NaN");
    LatLngWrapBounds.from(20, 20, Double.NaN, 0);
  }

  @Test
  public void testConstructorChecksWesttLongitudeNaN() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("longitude must not be NaN");
    LatLngWrapBounds.from(20, 20, 0, Double.NaN);
  }

  @Test
  public void testConstructorChecksSouthLatitudeGreaterThan90() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds.from(20, 20, 95, 0);
  }

  @Test
  public void testConstructorChecksSouthLatitudeLessThanThanNegative90() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latitude must be between -90 and 90");
    LatLngWrapBounds.from(20, 20, -95, 0);
  }

  @Test
  public void testConstructorChecksWestLongitudeInfinity() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("longitude must not be infinite");
    LatLngWrapBounds.from(20, 20, 0, Double.POSITIVE_INFINITY);
  }

  @Test
  public void testConstructorCheckLatSouthGreaterLatNorth() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("latNorth cannot be less than latSouth");
    LatLngWrapBounds.from(0, 20, 20, 0);
  }
}
