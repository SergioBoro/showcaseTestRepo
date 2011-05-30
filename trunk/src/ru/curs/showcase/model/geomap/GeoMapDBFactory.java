package ru.curs.showcase.model.geomap;

import java.sql.*;

import javax.sql.RowSet;

import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Фабрика для создания карт для информационной панели.
 * 
 * @author den
 * 
 */
public final class GeoMapDBFactory extends AbstractGeoMapFactory {
	static final String CODE_TAG = "Code";
	protected static final String IS_MAIN_TAG = "IsMain";
	static final String TOOLTIP_COL = "Tooltip";
	static final String WRONG_LAYER_ERROR =
		"В переданных данных найден объект, ссылающийся на несуществующий слой";
	static final String NO_IND_VALUES_TABLE_ERROR =
		"Не передана таблица со значениями показателей для объектов на карте";
	static final String NO_POINTS_TABLE_ERROR = "Не передана таблица с точками для карты";
	static final String WRONG_OBJ_ERROR =
		"В переданных данных найдено значение показателя, ссылающееся на несуществующий объект";
	static final String INDICATOR_ID = "IndicatorID";
	static final String POLYGON_TO_POINT_LAYER_ERROR =
		"В слой типа 'точки' нельзя добавлять области";

	static final String POINT_TO_POLYGON_LAYER_ERROR =
		"В слой типа 'области' нельзя добавлять точки";

	/**
	 * Таблица с данными о слоях.
	 */
	private RowSet layersSql;

	/**
	 * Таблица с данными о выделенных областях.
	 */
	private RowSet areasSql;

	/**
	 * Таблица с данными о точках.
	 */
	private RowSet pointsSql;

	/**
	 * Таблица с данными о показателях для всех слоев.
	 */
	private RowSet indicatorsSql;

	/**
	 * Таблица с данными о значениях показателей.
	 */
	private RowSet indicatorValuesSql;

	public GeoMapDBFactory(final ElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void fillLayers() throws SQLException {
		while (layersSql.next()) {
			String value = layersSql.getString(OBJECT_TYPE_TAG).toUpperCase().trim();
			GeoMapLayer layer = getData().addLayer(GeoMapFeatureType.valueOf(value));
			layer.setId(layersSql.getString(ID_TAG.toUpperCase()));
			layer.setName(layersSql.getString(NAME_TAG.toUpperCase()));
			if (SQLUtils.existsColumn(layersSql.getMetaData(),
					TextUtils.capitalizeWord(HINT_FORMAT_TAG))) {
				layer.setHintFormat(layersSql.getString(TextUtils.capitalizeWord(HINT_FORMAT_TAG)));
			}

		}
	}

	private GeoMapData getData() {
		return getResult().getJavaDynamicData();
	}

	@Override
	protected void fillPolygons() throws SQLException {
		while (areasSql.next()) {
			GeoMapLayer layer = getLayerForObject(areasSql);
			GeoMapFeature area =
				layer.addPolygon(areasSql.getString(ID_TAG.toUpperCase()),
						areasSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (area == null) {
				throw new InconsistentSettingsFromDBException(POLYGON_TO_POINT_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), CODE_TAG)) {
				area.setGeometryId(areasSql.getString(CODE_TAG));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), TextUtils.capitalizeWord(COLOR_TAG))) {
				area.setStyle(areasSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(),
					TextUtils.capitalizeWord(STYLE_CLASS_TAG))) {
				area.setStyleClass(areasSql.getString(TextUtils.capitalizeWord(STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), TOOLTIP_COL)) {
				String value = areasSql.getString(TOOLTIP_COL);
				if (value != "") {
					area.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(area.getId(), areasSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillPoints() throws SQLException {
		while (pointsSql.next()) {
			GeoMapLayer layer = getLayerForObject(pointsSql);
			GeoMapFeature point =
				layer.addPoint(pointsSql.getString(ID_TAG.toUpperCase()),
						pointsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (point == null) {
				throw new InconsistentSettingsFromDBException(POINT_TO_POLYGON_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), LAT_TAG)
					&& SQLUtils.existsColumn(pointsSql.getMetaData(), LON_TAG)) {
				Double[] coords = { pointsSql.getDouble(LON_TAG), pointsSql.getDouble(LAT_TAG) };
				point.getGeometry().setPointCoordinates(coords);

			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), TOOLTIP_COL)) {
				String value = pointsSql.getString(TOOLTIP_COL);
				if (value != "") {
					point.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(),
					TextUtils.capitalizeWord(STYLE_CLASS_TAG))) {
				point.setStyleClass(pointsSql.getString(TextUtils.capitalizeWord(STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(point.getId(), pointsSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillIndicators() throws SQLException {
		while (indicatorsSql.next()) {
			GeoMapLayer layer = getLayerForObject(indicatorsSql);
			GeoMapIndicator indicator =
				layer.addIndicator(indicatorsSql.getString(ID_TAG.toUpperCase()),
						indicatorsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(), IS_MAIN_TAG)) {
				indicator.setIsMain(indicatorsSql.getBoolean(IS_MAIN_TAG));
			}
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(),
					TextUtils.capitalizeWord(COLOR_TAG))) {
				indicator.setStyle(indicatorsSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
		}
	}

	@Override
	protected void fillIndicatorValues() throws SQLException {
		while (indicatorValuesSql.next()) {
			String objectId = indicatorValuesSql.getString(OBJECT_ID_TAG);
			GeoMapLayer layer = getData().getLayerByObjectId(objectId);
			if (layer == null) {
				throw new ResultSetHandleException(WRONG_OBJ_ERROR);
			}
			GeoMapFeature feature = layer.getObjectById(objectId);
			Double value = indicatorValuesSql.getDouble(TextUtils.capitalizeWord(VALUE_TAG));
			String dbId = indicatorValuesSql.getString(INDICATOR_ID);
			feature.setValue(layer.getAttrIdByDBId(dbId), value);
		}

	}

	private GeoMapLayer getLayerForObject(final RowSet rowset) throws SQLException {
		String layerId = rowset.getString(LAYER_ID_TAG);
		GeoMapLayer layer = getData().getLayerById(layerId);
		if (layer == null) {
			throw new ResultSetHandleException(WRONG_LAYER_ERROR);
		}
		return layer;
	}

	private void readEvents(final String objectId, final String value) {
		EventFactory<GeoMapEvent> factory = new EventFactory<GeoMapEvent>(GeoMapEvent.class);
		factory.initForGetSimpleSubSetOfEvents(MAP_PROPERTIES_XSD);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(objectId, value));
	}

	@Override
	protected void prepareData() {
		try {
			ResultSet rs = getStatement().getResultSet();
			layersSql = SQLUtils.cacheResultSet(rs);
			if (!getStatement().getMoreResults()) {
				throw new ResultSetHandleException(NO_POINTS_TABLE_ERROR);
			}
			rs = getStatement().getResultSet();
			pointsSql = SQLUtils.cacheResultSet(rs);
			if (!getStatement().getMoreResults()) {
				return; // разрешаем создавать карту, содержащую только точки -
						// например карту региона.
			}
			rs = getStatement().getResultSet();
			areasSql = SQLUtils.cacheResultSet(rs);
			if (!getStatement().getMoreResults()) {
				return; // разрешаем создавать карту без показателей
			}
			rs = getStatement().getResultSet();
			indicatorsSql = SQLUtils.cacheResultSet(rs);
			if (!getStatement().getMoreResults()) {
				throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
			}
			rs = getStatement().getResultSet();
			indicatorValuesSql = SQLUtils.cacheResultSet(rs);
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void prepareSettings() {
		getSource().prepareSettings();
	}

	@Override
	protected void releaseResources() {
		getSource().releaseResources();
	}

	private CallableStatement getStatement() {
		return getSource().getSpCallHelper().getCs();
	}

	@Override
	protected void fillResultByData() {
		try {
			super.fillResultByData();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

}
