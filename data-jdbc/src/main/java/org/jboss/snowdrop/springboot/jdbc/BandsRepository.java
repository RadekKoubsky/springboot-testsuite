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

import io.obsidian.testsuite.jpa.domain.Band;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Band} entries.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@Repository
public class BandsRepository {

	private static final String GET_BY_ID_QUERY = "select * from band where id=%d";

	private static final String CREATE_QUERY = "insert into band (name) values ('%s')";

	private JdbcTemplate jdbcTemplate;

	private RecordsRepository recordsRepository;

	@Autowired
	public BandsRepository(JdbcTemplate jdbcTemplate, RecordsRepository recordsRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.recordsRepository = recordsRepository;
	}

	public Band findById(Long id) {
		return this.jdbcTemplate.queryForObject(String.format(GET_BY_ID_QUERY, id), new BandRowMapper(this.recordsRepository));
	}

	public void insert(Band band) {
		this.jdbcTemplate.update(String.format(CREATE_QUERY, band.getName()));
	}

	private static class BandRowMapper implements RowMapper<Band> {

		private RecordsRepository recordsRepository;

		BandRowMapper(RecordsRepository recordsRepository) {
			this.recordsRepository = recordsRepository;
		}

		@Override
		public Band mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
			Band band = new Band();
			band.setId(resultSet.getLong("id"));
			band.setName(resultSet.getString("name"));
			band.setRecords(this.recordsRepository.findByBandId(band.getId()));
			return band;
		}

	}

}
