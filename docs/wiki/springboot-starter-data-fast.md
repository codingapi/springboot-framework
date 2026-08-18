springboot-starter-data-fast

基于JPA的快速API能力服务

## FastRepository 的使用教程

继承FastRepository接口，实现自定义的接口，即可使用FastRepository的能力
```java


import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface DemoRepository extends FastRepository<Demo,Integer> {

}


```

> 注：示例中的 `com.codingapi.springboot.fast.entity.Demo` 是模块的测试用示例实体，位于 `springboot-starter-data-fast` 的 `src/test/java` 目录下（对应表 `t_demo`）。实际使用时请定义自己的 `@Entity` 实体类并替换。

### FastRepository 能力概览

`FastRepository<T, ID>` 继承了 `JpaRepository`、`JpaSpecificationExecutor`、`DynamicRepository`（HQL 动态查询）与 `DynamicNativeRepository`（原生 SQL 查询），除 JPA 标准能力外还提供：

| 能力 | 方法 | 说明 |
|------|------|------|
| 条件过滤查询 | `findAll(PageRequest)` / `pageRequest(PageRequest)` | 根据 Filter 自动构建 Example 或动态 HQL 查询 |
| 高级搜索 | `searchRequest(SearchRequest)` | 基于 `SearchRequest`（前端检索条件对象）的分页查询 |
| 动态 HQL 查询 | `dynamicListQuery(...)` / `dynamicPageQuery(...)` | HQL 列表 / 分页查询，可配合 `SQLBuilder` 映射 DTO |
| Map 视图查询 | `dynamicMapListQuery(QueryColumns, sql, params...)` / `dynamicMapPageQuery(QueryColumns, sql, countSql, request, params...)` | 通过 `QueryColumns` 指定投影列，返回 `MapViewResult` |
| 原生 SQL 查询 | `dynamicNativeListQuery(...)` / `dynamicNativePageQuery(...)` / `dynamicNativeListMapQuery(...)` / `dynamicNativeMapPageMapQuery(...)` | `DynamicNativeRepository` 提供的原生 SQL 查询，支持实体映射或 `Map` 结果 |

动态FastRepository的能力展示

```

    // 重写findAll，通过Example查询 
    @Test
    void findAll() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", "123");

            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(1, page.getTotalElements());
        }


    // pageRequest 自定义条件查询        
    @Test
    void pageRequest1() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", Relation.LIKE, "%2%");
            //sql: select demo0_.id as id1_0_, demo0_.name as name2_0_, demo0_.sort as sort3_0_ from t_demo demo0_ where demo0_.name like ? limit ?

            Page<Demo> page = demoRepository.pageRequest(request);
            assertEquals(1, page.getTotalElements());
    }
    
    // pageRequest 自定义条件查询      
    @Test
    void pageRequest2() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.orFilters(Filter.as("name","123"),Filter.as("name","456"));
            //sql: select demo0_.id as id1_0_, demo0_.name as name2_0_, demo0_.sort as sort3_0_ from t_demo demo0_ where demo0_.name=? or demo0_.name=? limit ?

            Page<Demo> page = demoRepository.pageRequest(request);
            assertEquals(1, page.getTotalElements());
    }


    // 动态sql的List查询    
    @Test
    void dynamicListQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            List<Demo> list = demoRepository.dynamicListQuery("from Demo where name = ?1", "123");
            assertEquals(1, list.size());
        }


    // 动态sql的分页查询        
    @Test
    void dynamicPageQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            Page<Demo> page = demoRepository.dynamicPageQuery("from Demo where name = ?1", PageRequest.of(1, 2), "123");
            assertEquals(1, page.getTotalElements());
        }

    // 增加排序查询
    @Test
    void sortQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);

            request.addSort(Sort.by("id").descending());
            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(page.getContent().get(0).getName(), "456");
            assertEquals(2, page.getTotalElements());
        }

```

> 说明：示例中的 `PageRequest`、`Relation`、`Filter` 均来自核心模块 `springboot-starter`，需导入
> `com.codingapi.springboot.framework.dto.request.PageRequest` / `Relation` / `Filter`。
> 条件关系使用独立枚举 `com.codingapi.springboot.framework.dto.request.Relation`（如 `Relation.LIKE`、`Relation.IN`），
> 而非 `PageRequest` 的内部枚举。

## ScriptMapping 教程

通过动态添加mvc mapping实现查询功能.


构造签名：`ScriptMapping(String mapping, ScriptMethod scriptMethod, String script)`

* mapping 是mvc接口的访问地址
* scriptMethod 是mvc接口的请求方式，为枚举 `ScriptMethod.GET` / `ScriptMethod.POST`
* script 是接口执行的 Groovy 查询脚本内容

```java
import com.codingapi.springboot.fast.script.ScriptMapping;
import com.codingapi.springboot.fast.script.ScriptMethod;

// 定义接口执行的 Groovy 脚本
String script = """
        var name = $request.getParameter("name","");
        var sql = "select * from api_mapping where 1=1 ";
        var params = [];
        if(!"".equals(name)){
            sql += " and name = ? ";
            params.add(name);
        }
        return $jdbc.queryForMapList(sql, params.toArray());
        """;

// 注册一个 GET 方式的 /api/demo/list 查询接口
ScriptMapping scriptMapping = new ScriptMapping("/api/demo/list", ScriptMethod.GET, script);
fastScriptMappingRegister.addMapping(scriptMapping);
```

其中 `fastScriptMappingRegister` 为 `FastScriptMappingRegister` Bean，`addMapping(ScriptMapping)` 会将脚本动态注册为 MVC 接口；脚本执行结果会自动封装为 `Response`（List / Page 结果返回 `MultiResponse`，其余返回 `SingleResponse`）。

脚本实例代码： 
* 动态分页查询
```
// 获取name的请求参数
var name = $request.getParameter("name","");
var pageNumber = $request.getParameter("pageNumber",0);
var pageSize = $request.getParameter("pageSize",10);
// 创建分页对象
var pageRequest = $request.pageRequest(pageNumber,pageSize);
// 动态组织sql
var sql = "select * from api_mapping where 1 =1 ";
var countSql = "select count(1) from api_mapping where 1 =1 ";
// 动态组织参数
var params = [];
if(!"".equals(name)){
   sql += " and name = ? ";
   countSql += " and name = ? ";
   params.push(name);
}
sql += " limit ?,?";
// 添加分页参数
params.add(pageRequest.getOffset());
params.add(pageRequest.getPageSize());
// 执行分页查询（结果为 Map 列表，使用 queryForMapPage）
return $jdbc.queryForMapPage(sql,countSql,pageRequest,params.toArray());
```
* 动态条件查询
```
// 获取name的请求参数
var name = $request.getParameter("name","");
// 动态组织sql
String sql = "select * from api_mapping where 1=1 ";
// 动态组织参数
var params = [];
if(!"".equals(name)){
    sql += " and name = ? ";
    params.add(name);
}
// 执行查询（结果为 Map 列表，使用 queryForMapList）
return $jdbc.queryForMapList(sql,params.toArray());
```

脚本语法介绍：  
* $request
```
// 获取参数name的值，如果参数不存在，则返回默认值
var name = $request.getParameter("name","");
// 获取分页对象
var pageRequest = $request.pageRequest(0,10);
// 获取分页对象的页码
var pageNumber = pageRequest.getPageNumber();
// 获取分页对象的每页记录数
var pageSize = pageRequest.getPageSize();
// 获取分页对象的偏移量
var offset = pageRequest.getOffset();
```
* $jdbc（`JdbcQuery` 实例，常用方法签名）
```
// Map 列表查询：$jdbc.queryForMapList({sql}, {params}...)，结果列名自动转驼峰
// 实体映射列表查询：$jdbc.queryForList({sql}, {clazz}, {params}...)
// Map 分页查询：$jdbc.queryForMapPage({sql}, {countSql}, {pageRequest}, {params}...)
// 实体映射分页查询：$jdbc.queryForPage({sql}, {countSql}, {clazz}, {pageRequest}, {params}...)

// 查询无条件的数据（返回 List<Map<String,Object>>）
var res = $jdbc.queryForMapList("select * from api_mapping");
// 查询有条件的数据
var res = $jdbc.queryForMapList("select * from api_mapping where name = ?",name);
// 查询多条件的数据
var res = $jdbc.queryForMapList("select * from api_mapping where name = ? and url = ?",name,url);

// 需要将结果映射为实体类时，使用 queryForList(sql, Class, params...)
var res = $jdbc.queryForList("select * from api_mapping where name = ?",com.example.entity.ApiMapping.class,name);

// 分页查询（Map 结果） $jdbc.queryForMapPage({sql},{countSql},{pageRequest},{params}...)
var res = $jdbc.queryForMapPage("select * from api_mapping where name = ? and url = ?",
"select count(1) from api_mapping where name = ? and url = ?",pageRequest,name,url);
```
* $jpa
```
// 查询jpa $jpa.listQuery({clazz},{sql},{params})

// 查询无条件的数据
var res = $jpa.listQuery(com.example.entity.NodeEntity.class,"from NodeEntity");
// 查询有条件的数据
var res = $jpa.listQuery(com.example.entity.NodeEntity.class,"from NodeEntity where name = ?",name);
```