package work.inabajun.ranabauts.web

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import work.inabajun.ranabauts.domain.command.Command
import work.inabajun.ranabauts.web.command.CommandDeserializer

@EnableWebMvc
@Configuration
class WebConfig : WebMvcConfigurer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // for json
        val mapper = jacksonObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Command::class.java, CommandDeserializer())
        mapper.registerModule(module)
        converters.add(MappingJackson2HttpMessageConverter(mapper))

        logger.info("Setup Message converters for web.")
    }
}
