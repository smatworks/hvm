package net.smartworks.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import net.smartworks.dao.IHvmDao;
import net.smartworks.dao.mapper.HvmAttributeMapper;
import net.smartworks.dao.mapper.HvmProjectMapper;
import net.smartworks.model.hvm.HvmAttribute;
import net.smartworks.model.hvm.HvmAttributeCond;
import net.smartworks.model.hvm.HvmProject;
import net.smartworks.model.hvm.HvmProjectCond;

public class HvmDaoImpl implements IHvmDao {

	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplateObject;
   
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public Long getHvmProjectSize(String userId, HvmProjectCond cond) throws Exception {

		String searchKey = cond.getSearchKey();

		StringBuffer query = new StringBuffer();
		query.append("select count(*) ");
		
		setProjectQuery(query, cond);

		Long totalSize = 0L;
		if (searchKey != null && searchKey.length() != 0) {
			String likeSearchKey = "%" + searchKey + "%";
			totalSize = jdbcTemplateObject.queryForObject(query.toString(), new Object[]{likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey}, Long.class);
		} else {
			totalSize = jdbcTemplateObject.queryForObject(query.toString(), Long.class);
		}
		return totalSize;
	
	}
	
	private void setProjectQuery(StringBuffer query, HvmProjectCond cond) throws Exception {

		String searchKey = cond.getSearchKey();
		
		query.append(" from ");
		query.append(" hvmproject ");
		query.append(" where  ");
		query.append(" 1=1 ");
		if (searchKey != null) {
			query.append(" and id in ");
			query.append(" ( ");
			query.append(" 	select prj.id ");
			query.append(" 	from  ");
			query.append(" 	hvmattribute attr, hvmproject prj ");
			query.append(" 	where attr.prjid = prj.id ");
			query.append(" 	and 1=1 ");
			query.append(" 	group by prj.id ");
			query.append(" ) ");
		}
	}
	
	@Override
	public List<HvmProject> getHvmProjects(String userId, HvmProjectCond cond) throws Exception {

		int pageNo = cond.getPageNo();
		int pageSize = cond.getPageSize();
		int offSet = pageNo == 0 ? 0 : pageNo * pageSize;
		
		String searchKey = cond.getSearchKey();
		
		
		StringBuffer query = new StringBuffer();
		query.append(" select * ");
		
		setProjectQuery(query, cond);
		
		query.append(" limit ? ");
		query.append(" offset ? ");
		
		List<HvmProject> projectList = jdbcTemplateObject.query(query.toString(), 
				new PreparedStatementSetter(){
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						if (searchKey != null && searchKey.length() != 0) {
							String likeSearchKey = "%" + searchKey + "%";
							/*preparedStatement.setString(1, likeSearchKey);
							preparedStatement.setString(2, likeSearchKey);
							preparedStatement.setString(3, likeSearchKey);
							preparedStatement.setString(4, likeSearchKey);
							preparedStatement.setString(5, likeSearchKey);
							preparedStatement.setString(6, likeSearchKey);*/
							preparedStatement.setInt(7, pageSize);
							preparedStatement.setInt(8, offSet);
						} else {
							preparedStatement.setInt(1, pageSize);
							preparedStatement.setInt(2, offSet);
						}
		            }
				}
				, new HvmProjectMapper());
		
		if (projectList == null || projectList.size() == 0)
			return null;
		
		StringBuffer attrQuery = new StringBuffer();
		attrQuery.append(" select * from HvmAttribute where prjid=? order by valuename");
		
		//HvmProject[] projects = new HvmProject[projectList.size()];
		
		for (int i = 0; i < projectList.size(); i++) {
			HvmProject project = projectList.get(i);
			//attribute 조회
			HvmAttribute[] attrs = null;
			
			String projectId = project.getId();
			
			List<HvmAttribute> attrList = jdbcTemplateObject.query(attrQuery.toString(), 
					new PreparedStatementSetter(){
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1, projectId);
			            }
					}
					, new HvmAttributeMapper());
			
			//
			
			project.setAttributes(attrList);
			//projects[i] = project;
		}
		//return projects;
		return projectList;
	}
	
	@Override
	public boolean setHvmProject(String userId, HvmProject prj) throws Exception {
		
		if (prj == null)
			return false;
		
		this.removeHvmProject(userId, prj.getId());
		
		String prjSql = "insert into hvmproject (id, pssPrjId, pssPrjName, pssPrjDescription, pssPrjPicture"
				+ ", sbpPrjId, sbpPrjName, createdUser, lastModifiedUser, createdDate ,lastModifiedDate) "
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	      
	    jdbcTemplateObject.update(prjSql
	    		, prj.getId()
	    		, prj.getPssPrjId()
	    		, prj.getPssPrjName()
	    		, prj.getPssPrjDescription()
	    		, prj.getPssPrjPicture()
	    		, prj.getSbpPrjId()
	    		, prj.getSbpPrjName()
	    		, prj.getCreatedUser()
	    		, prj.getLastModifiedUser()
	    		, prj.getCreatedDate()
	    		, prj.getLastModifiedDate()
	    		);
		
		List<HvmAttribute> attrs = prj.getAttributes();
		if (attrs != null) {
			StringBuffer attrSql = new StringBuffer().append("insert into hvmattribute ");
			attrSql.append(" (id, prjid, valueid, valuename, sbpid, sbpname, activityid, activityname, attributetype, attributename) ");
			attrSql.append(" values (? ,? ,? ,? ,? ,? ,? ,? ,? ,?)");

			
			jdbcTemplateObject.batchUpdate(attrSql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					HvmAttribute attribute = attrs.get(i);
					ps.setString(1, attribute.getId());
					ps.setString(2, attribute.getPrjId());
					ps.setString(3, attribute.getValueId());
					ps.setString(4, attribute.getValueName());
					ps.setString(5, attribute.getSbpId());
					ps.setString(6, attribute.getSbpName());
					ps.setString(7, attribute.getActivityId());
					ps.setString(8, attribute.getActivityName());
					ps.setString(9, attribute.getAttributeType());
					ps.setString(10, attribute.getAttributeName());
				}
				@Override
				public int getBatchSize() {
					return attrs.size();
				}
			});
		}
		return true;
	}

	@Override
	public boolean removeHvmProject(String userId, String prjId) throws Exception {
		
		StringBuffer attrQuery = new StringBuffer().append("delete from hvmattribute where prjid = ? ");
		StringBuffer prjQuery = new StringBuffer().append("delete from hvmproject where id=?");

		jdbcTemplateObject.update(attrQuery.toString(), prjId);
		jdbcTemplateObject.update(prjQuery.toString(), prjId);
		
		return true;
	}
	

	private void setAttributeQuery(StringBuffer query, HvmAttributeCond cond) throws Exception {
		
		String searchKey = cond.getSearchKey();
		
		query.append(" from ");
		query.append(" hvmattribute ");
		query.append(" where  ");
		query.append(" 1=1 ");
		if (searchKey != null) {
			query.append(" and attributename like ?");
		}
	}
	@Override
	public Long getHvmAttributeSize(String userId, HvmAttributeCond cond) throws Exception {

		String searchKey = cond.getSearchKey();

		StringBuffer query = new StringBuffer();
		query.append("select count(*) ");
		
		setAttributeQuery(query, cond);

		Long totalSize = 0L;
		if (searchKey != null && searchKey.length() != 0) {
			String likeSearchKey = "%" + searchKey + "%";
			totalSize = jdbcTemplateObject.queryForObject(query.toString(), new Object[]{likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey,likeSearchKey}, Long.class);
		} else {
			totalSize = jdbcTemplateObject.queryForObject(query.toString(), Long.class);
		}
		return totalSize;
	
	}
	
	@Override
	public List<HvmAttribute> getHvmAttributes(String userId, HvmAttributeCond cond) throws Exception {
		int pageNo = cond.getPageNo();
		int pageSize = cond.getPageSize();
		int offSet = pageNo == 0 ? 0 : pageNo * pageSize;
		
		String searchKey = cond.getSearchKey();
		
		
		StringBuffer query = new StringBuffer();
		query.append(" select * ");
		
		setAttributeQuery(query, cond);
		
		query.append(" limit ? ");
		query.append(" offset ? ");
		
		List<HvmAttribute> attrList = jdbcTemplateObject.query(query.toString(), 
				new PreparedStatementSetter(){
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						if (searchKey != null && searchKey.length() != 0) {
							String likeSearchKey = "%" + searchKey + "%";
							/*preparedStatement.setString(1, likeSearchKey);
							preparedStatement.setString(2, likeSearchKey);
							preparedStatement.setString(3, likeSearchKey);
							preparedStatement.setString(4, likeSearchKey);
							preparedStatement.setString(5, likeSearchKey);
							preparedStatement.setString(6, likeSearchKey);*/
							preparedStatement.setInt(7, pageSize);
							preparedStatement.setInt(8, offSet);
						} else {
							preparedStatement.setInt(1, pageSize);
							preparedStatement.setInt(2, offSet);
						}
		            }
				}
				, new HvmAttributeMapper());
		
		return attrList;
	}
	
}
