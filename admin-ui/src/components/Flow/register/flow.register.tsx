import {registerComponent} from "@/framework/ComponentBus";
import PostponedFormView from "@/components/Flow/components/PostponedFormView";
import ResultFormView from "@/components/Flow/components/ResultFormView";
import {PostponedFormViewKey, ResultFormViewKey, UserSelectViewKey} from "@/components/Flow/flow/types";
import UserSelectView from "@/components/Flow/components/UserSelectView";

registerComponent(PostponedFormViewKey, PostponedFormView);
registerComponent(ResultFormViewKey, ResultFormView);
registerComponent(UserSelectViewKey, UserSelectView);
