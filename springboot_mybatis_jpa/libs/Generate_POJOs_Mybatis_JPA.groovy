import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
 * IDEA 生成 bean dao service
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 *
 * 接口源码 IDEA lib/src/src_database-openapi.zip
 */
author = "ggunlics"
date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)

BService = true
BMapper = true
BJap = false

// 基础包
commonPackage = "com.emoon.modules.iapp.modules.recevingsite."

entityPath = "/pojo/entity/"
entityPackage = commonPackage + "pojo.entity;"

mapperPath = "/dao/mapper/"
mapperPackage = commonPackage + "dao.mapper;"

repositoryPath = "/dao/repository/"
repositoryPackage = commonPackage + "dao.repository;"

servicePath = "/service/"
servicePackage = commonPackage + "service;"

serviceImplPath = "/service/impl/"
serviceImplPackage = commonPackage + "service.impl;"

createTime = "create_time"
updateTime = "update_time"

hasKey = false

/**
 * 类型转换
 */
typeMapping = [
        (~/(?i)tinyint/)           : "Integer",
        (~/(?i)int/)               : "Long",
        (~/(?i)float/)             : "Float",
        (~/(?i)decimal|real/)      : "BigDecimal",
        (~/(?i)double/)            : "Double",
        (~/(?i)datetime|timestamp/): "LocalDateTime",
        (~/(?i)date/)              : "LocalDate",
        (~/(?i)time/)              : "LocalTime",
        (~/(?i)/)                  : "String"
]

/**
 * FILES.chooseDirectoryAndSave 是在 idea 的 Database 窗口鼠标右键点击 groovy 选项后弹出文件夹选择框关闭时回调的方法，
 * DasTable 指代一张表，保存了该张表中的一些信息，如表名，字段等，dir 是选中的文件夹。
 */
FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

/**
 * 根据 table 和 dir 生成目标文件
 * @param table 表
 * @param dir 目录
 * @return
 */
def generate(table, dir) {
    def className = javaName(table.getName(), true)

    def fields = calcFields(table)
    def keyField = fields.find { it -> it.key }

    // 生成entity
    def entityDir = new File(dir.getPath() + entityPath)
    if (!entityDir.exists()) {
        entityDir.mkdirs()
    }
    new File(entityDir, className + ".java").withPrintWriter("utf-8") { out -> generateEntity(out, className, fields, table) }

    if (BMapper) {
        // 生成mapper
        def mapperDir = new File(dir.getPath() + mapperPath)
        if (!mapperDir.exists()) {
            mapperDir.mkdirs()
        }
        new File(mapperDir, className + "Mapper.java").withPrintWriter("utf-8") { out -> generateMapper(out, className, table) }
    }

    if (BJap) {
        // 生成repository
        def repositoryDir = new File(dir.getPath() + repositoryPath)
        if (!repositoryDir.exists()) {
            repositoryDir.mkdirs()
        }
        new File(repositoryDir, className + "Repository.java").withPrintWriter("utf-8") { out -> generateRepository(out, className, table, keyField) }
    }

    if (BService) {
        // 生成service
        def serviceDir = new File(dir.getPath() + servicePath)
        if (!serviceDir.exists()) {
            serviceDir.mkdirs()
        }
        new File(serviceDir, "I" + className + "Service.java").withPrintWriter("utf-8") { out -> generateService(out, className, table) }

        // 生成service impl
        def serviceImplDir = new File(dir.getPath() + serviceImplPath)
        if (!serviceImplDir.exists()) {
            serviceImplDir.mkdirs()
        }
        new File(serviceImplDir, className + "ServiceImpl.java").withPrintWriter("utf-8") { out -> generateServiceImpl(out, className, table) }
    }

}

/**
 * 正真进行模板生成并写入文件
 * @param out 输出文件夹
 * @param className 文件名
 * @param fields 字段
 * @return
 */
def generateEntity(out, className, fields, table) {
    out.println "package $entityPackage"
    out.println ""
    out.println "import javax.persistence.*;"
    out.println "import java.io.Serializable;"
    out.println "import lombok.AllArgsConstructor;"
    out.println "import lombok.Data;"
    out.println "import lombok.NoArgsConstructor;"
    if (BJap) {
        out.println "import org.hibernate.annotations.DynamicInsert;"
        out.println "import org.hibernate.annotations.DynamicUpdate;"
    }
    if (BMapper) {
        out.println "import com.baomidou.mybatisplus.annotation.TableName;"
    }

    // 有主键
    if (hasKey) {
        if (BJap) {
            out.println "import com.baomidou.mybatisplus.annotation.IdType;"
        }
        if (BMapper) {
            out.println "import com.baomidou.mybatisplus.annotation.TableId;"
        }
    }

    // 引入非基本类
    Set types = new HashSet()
    fields.each() {
        types.add(it.type)
    }

    def haveDateType = false

    if (types.contains("LocalDateTime")) {
        haveDateType = true
        out.println "import java.time.LocalDateTime;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;"
    }
    if (types.contains("LocalDate")) {
        haveDateType = true
        out.println "import java.time.LocalDate;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;"
    }
    if (types.contains("LocalTime")) {
        haveDateType = true
        out.println "import java.time.LocalTime;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;"
        out.println "import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;"
    }

    if (haveDateType) {
        out.println "import com.fasterxml.jackson.databind.annotation.JsonDeserialize;"
        out.println "import com.fasterxml.jackson.databind.annotation.JsonSerialize;"
        out.println "import com.fasterxml.jackson.annotation.JsonFormat;"
    }

    if (types.contains("BigDecimal")) {
        out.println "import java.math.BigDecimal;"
    }

    out.println ""
    out.println "/**\n" +
            " * ${table.getComment()} \n" +
            " *\n" +
            " * @author $author\n" +
            " * @date $date\n" +
            " */"
    out.println "@Data"
    out.println "@NoArgsConstructor"
    out.println "@AllArgsConstructor"
    if (BJap) {
        out.println "@Entity"
        out.println "@Table(name = \"${table.getName()}\")"
        out.println "@DynamicUpdate"
        out.println "@DynamicInsert"
    }
    if (BMapper) {
        out.println "@TableName(value = \"${table.getName()}\")"
    }
    out.println "public class $className  implements Serializable {"
    fields.each() {
        out.println ""
        // 输出注释
        out.println "\t/**"
        out.println "\t * ${it.comment}"
        out.println "\t */"

        if (it.annos != "") out.println "   ${it.annos}"

        // 输出成员变量
        out.println "\tprivate ${it.type} ${it.name};"
    }
    out.println ""
    out.println ""
    out.println "}"
}

/**
 * col信息处理
 * @param table 表
 * @return
 */
def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        // 数据库类型
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        // java类型
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value

        def annos = "\t@Column(name = \"" + col.getName() + "\" "

        if (col.getName() == createTime || col.getName() == updateTime) {
            annos += ", insertable = false, updatable = false"
        }

        annos += ")"
        // col信息
        def comm = [
                colName: col.getName(),
                name   : javaName(col.getName(), false),
                type   : typeStr,
                comment: col.getComment(),
                annos  : annos,
                key    : false]

        // 列定义  主键、自增之类的 详情见 DasColumn.Attribute
        def colAttr = table.getColumnAttrs(col)
        // 主键识别 默认自增
        if (colAttr.contains(DasColumn.Attribute.PRIMARY_KEY)) {
            if (BJap) {
                comm.annos += "\n\t@Id\n"
                comm.annos += "\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n"
            }
            if (BMapper) {
                comm.annos += "\t@TableId(value = \"${col.getName()}\",type= IdType.AUTO)"
            }
            setHasKey(true)
            comm.key = true
        }

        if (typeStr == "LocalDateTime") {
            comm.annos += "\n\t@JsonDeserialize(using = LocalDateTimeDeserializer.class)\n" +
                    "\t@JsonSerialize(using = LocalDateTimeSerializer.class)" +
                    "\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")"
        }
        if (typeStr == "LocalDate") {
            comm.annos += "\n\t@JsonDeserialize(using = LocalDateDeserializer.class)\n" +
                    "\t@JsonSerialize(using = LocalDateSerializer.class)" +
                    "\t@JsonFormat(pattern = \"yyyy-MM-dd\",timezone = \"GMT+8\")"
        }
        if (typeStr == "LocalTime") {
            comm.annos += "\n\t@JsonDeserialize(using = LocalTimeDeserializer.class)\n" +
                    "\t@JsonSerialize(using = LocalTimeSerializer.class)" +
                    "\t@JsonFormat(pattern = \"HH:mm:ss\",timezone = \"GMT+8\")"
        }

        fields += [comm]
    }
}

/**
 * 处理类名（这里是因为我的表都是以t_命名的，所以需要处理去掉生成类名时的开头的T，
 * 如果你不需要那么请查找用到了 javaClassName这个方法的地方修改为 javaName 即可）
 * @param str
 * @param capitalize
 * @return
 */
def javaClassName(str, capitalize) {
    def s = str.split(/[^\p{Alnum}]/).collect { def s = Case.LOWER.apply(it).capitalize() }.join("")
    // 去除开头的T  http://developer.51cto.com/art/200906/129168.htm
    s = s[1..s.size() - 1]
    capitalize ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

/**
 * 下标字符串转驼峰
 * @param str 下标字符串
 * @param capitalize 首字符是否大写
 * @return void
 */
def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

/**
 * 是否为空
 * @param content 内容
 * @return boolean
 */
def isNotEmpty(content) {
    return content != null && content.toString().trim().length() > 0
}

/**
 * 设置是否有主键
 * @param key 是否有主键
 * @return void
 */
def setHasKey(key) {
    hasKey = key
}

/**
 * 字符串命名规则
 * @param str 字符串
 * @param toCamel 驼峰
 * @return str
 */
static String changeStyle(String str, boolean toCamel) {
    if (!str || str.size() <= 1)
        return str

    if (toCamel) {
        String r = str.toLowerCase().split('_').collect { cc -> Case.LOWER.apply(cc).capitalize() }.join('')
        return r[0].toLowerCase() + r[1..-1]
    } else {
        str = str[0].toLowerCase() + str[1..-1]
        return str.collect { cc -> ((char) cc).isUpperCase() ? '_' + cc.toLowerCase() : cc }.join('')
    }
}

/**
 * 生成mapper
 * @param out 目标文件
 * @param className 文件名称
 * @param table 表
 * @return
 */
def generateMapper(out, className, table) {
    out.println "package $mapperPackage"
    out.println ""
    out.println "import com.baomidou.mybatisplus.core.mapper.BaseMapper;"
    out.println "import ${entityPackage[0..-2]}.${className};"
    out.println ""
    out.println "/**\n" +
            " * ${table.getComment()} \n" +
            " *\n" +
            " * @author $author\n" +
            " * @date $date\n" +
            " */"
    out.println "public interface ${className}Mapper extends BaseMapper<${className}> {"
    out.println ""
    out.println "}"
}

/**
 * 生成repository
 * @param out 目标文件
 * @param className 文件名称
 * @param table 表
 * @param key 主键类型
 * @return
 */
def generateRepository(out, className, table, key) {
    out.println "package $repositoryPackage"
    out.println ""
    out.println "import org.springframework.data.jpa.repository.JpaRepository;"
    out.println "import ${entityPackage[0..-2]}.${className};"
    out.println ""
    out.println "/**\n" +
            " * ${table.getComment()} \n" +
            " *\n" +
            " * @author $author\n" +
            " * @date $date\n" +
            " */"
    out.println "public interface ${className}Repository extends JpaRepository<${className},${key == null ? 'String' : key.type}> {"
    out.println ""
    out.println "}"
}

/**
 * 生成service
 * @param out 目标文件
 * @param className 文件名称
 * @param table 表
 * @return
 */
def generateService(out, className, table) {
    out.println "package $servicePackage"
    out.println ""
    out.println "/**\n" +
            " * ${table.getComment()} \n" +
            " *\n" +
            " * @author $author\n" +
            " * @date $date\n" +
            " */"
    out.println "public interface I${className}Service {"
    out.println ""
    out.println "}"
}

/**
 * 生成service impl
 * @param out 目标文件
 * @param className 文件名称
 * @param table 表
 * @return
 */
def generateServiceImpl(out, className, table) {
    out.println "package $serviceImplPackage"
    out.println ""
    out.println "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;"
    out.println "import lombok.RequiredArgsConstructor;"
    out.println "import org.springframework.beans.factory.annotation.Autowired;"
    out.println "import org.springframework.stereotype.Service;"
    out.println "import lombok.extern.slf4j.Slf4j;"
    out.println "import ${mapperPackage[0..-2]}.${className}Mapper;"
    out.println "import ${entityPackage[0..-2]}.${className};"
    out.println "import ${servicePackage[0..-2]}.I${className}Service;"
    out.println ""
    out.println "/**\n" +
            " * ${table.getComment()} \n" +
            " *\n" +
            " * @author $author\n" +
            " * @date $date\n" +
            " */"
    out.println "@Slf4j"
    out.println "@Service"
    out.println "@RequiredArgsConstructor(onConstructor_ = {@Autowired})"
    out.println "public class ${className}ServiceImpl extends ServiceImpl<${className}Mapper, ${className}> implements I${className}Service {"
    out.println ""
    out.println "}"
}


