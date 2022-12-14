package run.halo.tocbot;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

/**
 * tocbot 插件
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class TocbotHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public TocbotHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch("basic", BasicConfig.class)
                .map(config -> {
                    final IModelFactory modelFactory = context.getModelFactory();
                    model.add(modelFactory.createText(tocbotScript(config)));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    private String tocbotScript(BasicConfig config) {

        String tocbotInit = buildTocInit(config);
        // language=html

        return """
                <script src="/plugins/PluginTocbot/assets/static/tocbot/4.18.2/tocbot.min.js"></script>
                <link href="/plugins/PluginTocbot/assets/static/tocbot/4.18.2/tocbot.css" rel="stylesheet">
                <script>
                    document.addEventListener("DOMContentLoaded",  function() {
                    
                        const postContent = document.querySelector('%s');
                    
                        if (postContent == null) return;
                    
                        const headers = postContent.querySelectorAll('%s');
                        // 没有 toc 目录，则直接移除
                        if (headers.length === 0) {
                            document.getElementById("%s").remove();
                        } else {
                            %s
                        }
                    })
                </script>
                                
                """.formatted(config.getPostContent(), config.getHeaderEl(), config.getRemoveContentId(), tocbotInit);
    }


    /**
     * 构建 tocInit
     */
    private String buildTocInit(BasicConfig config) {

        if (StringUtils.isNotBlank(config.getOptions())) {
            return """
                tocbot.init(%s);
                """.formatted(config.getOptions());
        }

        // language=javascript
        return  """
                tocbot.init({
                      tocSelector: '%s',
                      contentSelector: '%s',
                      headingSelector: '%s',
                      hasInnerContainers: true
                  });
                """.formatted(config.getTocContent(), config.getPostContent(), config.getHeaderEl());

    }

    /**
     * 基础配置
     */
    @Data
    public static class BasicConfig {

        /**
         * header 标签
         */
        String headerEl;

        /**
         * 文章容器
         */
        String postContent;

        /**
         * 目录容器
         */
        String tocContent;

        /**
         * 需要移除的容器
         */
        String removeContentId;

        /**
         * 自定义配置 json 格式
         */
        String options;
    }
}
