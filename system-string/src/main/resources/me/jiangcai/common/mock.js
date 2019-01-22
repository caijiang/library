/**
 * Created by CJ on 20/07/2017.
 */
Mock.setup({
    timeout: '1000'
});

Mock.mock(/\/test\/.+/, "put", {});
Mock.mock(/\/test\/.+/, "delete", {});