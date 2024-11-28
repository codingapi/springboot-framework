import {registerComponent} from "@/framework/ComponentBus";
import PostponedFormView from "@/components/Flow/components/PostponedFormView";
import ResultFormView from "@/components/Flow/components/ResultFormView";
import {PostponedFormViewKey, ResultFormViewKey} from "@/components/Flow/flow/types";

registerComponent(PostponedFormViewKey, PostponedFormView);
registerComponent(ResultFormViewKey, ResultFormView);
