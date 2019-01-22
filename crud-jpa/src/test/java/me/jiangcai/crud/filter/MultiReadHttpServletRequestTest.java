package me.jiangcai.crud.filter;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MultiReadHttpServletRequestTest {

    @Test
    public void work() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[]{1});
        MultiReadHttpServletRequest multiReadHttpServletRequest = new MultiReadHttpServletRequest(request);

        ServletInputStream servletInputStream = multiReadHttpServletRequest.getInputStream();
        assertThat(servletInputStream.isReady())
                .isTrue();
        assertThat(servletInputStream.isFinished())
                .isFalse();
        assertThat(servletInputStream.read())
                .isEqualTo(1);
        assertThat(servletInputStream.isFinished())
                .isTrue();
    }

}