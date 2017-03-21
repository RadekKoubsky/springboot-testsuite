/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.snowdrop.springboot.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jboss.snowdrop.springboot.common.entities.Record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Record} entries.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@Repository
public class RecordsRepository {

	private static final String GET_BY_BAND_ID_QUERY = "select * from record where bandId=%d";

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public RecordsRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Record> findByBandId(Long bandId) {
		return this.jdbcTemplate.query(String.format(GET_BY_BAND_ID_QUERY, bandId), new RecordRowMapper());
	}

	private static class RecordRowMapper implements RowMapper<Record> {

		@Override
		public Record mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
			Record record = new Record();
			record.setId(resultSet.getLong("id"));
			record.setName(resultSet.getString("name"));
			record.setBandId(resultSet.getLong("bandId"));
			return record;
		}

	}

}
