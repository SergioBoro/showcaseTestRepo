package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.LegendPosition;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.model.geomap.*;

/**
 * Тесты для фабрики карт.
 * 
 * @author den
 * 
 */
public class GeoMapFactoryTest extends AbstractTestBasedOnFiles {
	static final String RU_AL_ID = "2";

	/**
	 * Простой тест на создание объекта карты.
	 */
	@Test
	public void testSimpleCreateObj() {
		GeoMap map = new GeoMap(new GeoMapData());
		assertNotNull(map.getJavaDynamicData());
		assertNull(map.getJsDynamicData());
		map.getJavaDynamicData().addLayer(GeoMapFeatureType.POLYGON);
		assertEquals(GeoMapFeatureType.POLYGON, map.getJavaDynamicData().getLayers().get(0)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(0).getFeatures());
		GeoMapLayer layer = map.getJavaDynamicData().addLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapFeatureType.POINT, map.getJavaDynamicData().getLayers().get(1)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(1).getFeatures());
		assertNull(layer.addPolygon("aa", "bb"));
		final String pointId = "pointId1";
		final String pointName = "Москва";
		assertNotNull(layer.addPoint(pointId, pointName));
		GeoMapFeature feature = layer.getFeatures().get(0);
		assertEquals(pointId, feature.getId());
		assertEquals(pointName, feature.getName());
		final String indId = "12345";
		final String indName = "Надои";
		GeoMapIndicator ind = layer.addIndicator(indId, indName);
		assertNotNull(ind);
		assertEquals(indId, ind.getId());
		assertEquals(indName, layer.getIndicatorById(indId).getName());
		final double value = 1.0;
		feature.setValue(indId, value);
		assertEquals(value, feature.getValueForIndicator(ind).doubleValue(), 0);
		assertEquals(false, ind.getIsMain());
	}

	/**
	 * Тест на проверку статических свойств карты, созданной на основе данных из
	 * БД.
	 * 
	 * @throws IOException
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testFromDBStaticData() throws GeneralServerException, IOException {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		GeoMap map = serviceLayer.getGeoMap(context, element);
		assertNotNull(context.getSession());
		assertNotNull(map);

		assertNotNull(map.getHeader());
		assertNotNull(map.getFooter());

		Action action = map.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		assertNotNull(action.getDataPanelLink());
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals(context.getMain(), action.getDataPanelLink().getContext().getMain());
		assertEquals(NavigatorActionType.CHANGE_NODE, action.getNavigatorActionType());
		assertNotNull(action.getNavigatorElementLink());
		assertEquals("9EF5F299-0AB3-486B-A810-5818D17047AC", action.getNavigatorElementLink()
				.getId());

		assertEquals(LegendPosition.BOTTOM, map.getLegendPosition());
		assertNotNull(map.getJsDynamicData());
		assertNull(map.getJavaDynamicData());

		assertEquals(map.getActionForDependentElements(), map.getDefaultAction());
	}

	/**
	 * Тест на проверку динамических свойств карты, созданной на основе данных
	 * из БД.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFromDBDynamicData() throws Exception {
		CompositeContext context = getContext("tree_multilevel.xml", 0, 0);
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		GeoMapGateway gateway = new GeoMapDBGateway();
		ElementRawData raw = gateway.getFactorySource(context, element);
		GeoMapDBFactory factory = new GeoMapDBFactory(raw);
		GeoMap map = factory.build();

		final int size = 500;
		GeoMapData data = map.getJavaDynamicData();
		assertEquals(size, data.getHeight().intValue());
		assertEquals(size, data.getWidth().intValue());
		assertEquals(2, data.getLayers().size());
		assertNotNull(data.getLayerById("l1"));
		assertNotNull(data.getLayerById("l2"));
		assertNull(data.getLayerById("l3"));
		assertNotNull(data.getLayerByObjectId("1849"));
		GeoMapLayer layer = data.getLayerByObjectId(RU_AL_ID);
		assertNotNull(layer);
		assertNull(layer.getProjection());
		assertNull(data.getLayerByObjectId("blaaa"));
		assertNull(layer.getHintFormat());
		assertEquals(GeoMapFeatureType.POLYGON, layer.getType());
		final int areasCount = 5;
		assertEquals(areasCount, layer.getFeatures().size());
		assertEquals(2, layer.getIndicators().size());
		GeoMapFeature altay = layer.getObjectById(RU_AL_ID);
		assertEquals("Алтай - регион с показателями", altay.getTooltip());
		assertEquals("#FAEC7B", altay.getStyle());
		assertEquals(altay.getGeometryId(), altay.getStyleClass());
		final int indValue1 = 1000;
		assertNotSame("ind1", layer.getIndicators().get(0).getId());
		assertEquals("ind0", layer.getAttrIdByDBId("ind1"));
		assertEquals(false, layer.getIndicators().get(0).getIsMain());
		assertEquals("#2AAA2E", layer.getIndicators().get(0).getStyle());
		assertEquals(indValue1, altay.getValueForIndicator(layer.getIndicators().get(0))
				.doubleValue(), 0);
		final int indValue2 = 10;
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getIndicators().get(1).getId());
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getAttrIdByDBId("ind2"));
		assertEquals(layer.getMainIndicator(), layer.getIndicators().get(1));
		assertEquals(true, layer.getIndicators().get(1).getIsMain());
		assertEquals(indValue2, altay.getValueForIndicator(layer.getIndicators().get(1))
				.doubleValue(), 0);
		layer = data.getLayerById("l1");
		GeoMapFeature novgorod = layer.getObjectById("2532");
		assertEquals(
				String.format("%s - %s (%s) (%s - %s)", layer.getName(), novgorod.getName(),
						novgorod.getId(), novgorod.getGeometry().getLat(), novgorod.getGeometry()
								.getLon()), novgorod.getTooltip());
		assertEquals("TestStyleClass", novgorod.getStyleClass());
	}

	/**
	 * Проверка автоматической установки проекции слоя.
	 */
	@Test
	public void testLayerProjection() {
		GeoMapLayer layer = new GeoMapLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.POLYGON);
		assertNull(layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.MULTIPOLYGON);
		assertNull(layer.getProjection());
		layer.setType(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
	}

}
